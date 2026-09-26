package com.sece.block.service;

import com.sece.block.entity.BlockchainBlock;
import com.sece.block.entity.Certificate;
import com.sece.block.repository.BlockchainBlockRepository;
import com.sece.block.repository.CertificateRepository;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.List;

// This service creates a simple blockchain simulation for certificate verification.
@Service
public class BlockchainService {

    private static final String GENESIS_HASH = "0";

    private final BlockchainBlockRepository blockchainBlockRepository;
    private final CertificateRepository certificateRepository;

    public BlockchainService(BlockchainBlockRepository blockchainBlockRepository,
                              CertificateRepository certificateRepository) {
        this.blockchainBlockRepository = blockchainBlockRepository;
        this.certificateRepository = certificateRepository;
    }

    public List<BlockchainBlock> getBlockchain() {
        return blockchainBlockRepository.findAllByOrderByBlockIndexAsc();
    }

    public BlockchainBlock createGenesisBlock() {
        String timestamp = String.valueOf(Instant.now().getEpochSecond());
        String previousHash = GENESIS_HASH;
        String currentHash = calculateHash("GENESIS" + timestamp + previousHash);

        BlockchainBlock block = new BlockchainBlock();
        block.setBlockIndex(0);
        block.setTimestamp(timestamp);
        block.setCertificateId("GENESIS");
        block.setCertificateHash("GENESIS");
        block.setPreviousHash(previousHash);
        block.setCurrentHash(currentHash);

        return blockchainBlockRepository.save(block);
    }

    public BlockchainBlock addCertificateBlock(Certificate certificate) {
        List<BlockchainBlock> blockchain = blockchainBlockRepository.findAllByOrderByBlockIndexAsc();

        String previousHash = blockchain.isEmpty() ? GENESIS_HASH : blockchain.get(blockchain.size() - 1).getCurrentHash();
        String timestamp = String.valueOf(Instant.now().getEpochSecond());
        String currentHash = calculateHash(certificate.getCertificateId() + certificate.getCertificateHash() + previousHash + timestamp);

        BlockchainBlock block = new BlockchainBlock();
        block.setBlockIndex(blockchain.size());
        block.setTimestamp(timestamp);
        block.setCertificateId(certificate.getCertificateId());
        block.setCertificateHash(certificate.getCertificateHash());
        block.setPreviousHash(previousHash);
        block.setCurrentHash(currentHash);

        BlockchainBlock savedBlock = blockchainBlockRepository.save(block);

        certificate.setBlockchainHash(currentHash);
        certificate.setBlockIndex(savedBlock.getBlockIndex());
        certificate.setStatus("VALID");
        certificateRepository.save(certificate);

        return savedBlock;
    }

    public String calculateHash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            StringBuilder builder = new StringBuilder();
            for (byte currentByte : hashBytes) {
                String hex = Integer.toHexString(0xff & currentByte);
                if (hex.length() == 1) {
                    builder.append('0');
                }
                builder.append(hex);
            }

            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm is not available.", e);
        }
    }

    public boolean verifyBlockchain() {
        List<BlockchainBlock> blockchain = blockchainBlockRepository.findAllByOrderByBlockIndexAsc();

        if (blockchain.isEmpty()) {
            return true;
        }

        for (int i = 1; i < blockchain.size(); i++) {
            BlockchainBlock previous = blockchain.get(i - 1);
            BlockchainBlock current = blockchain.get(i);

            if (!previous.getCurrentHash().equals(current.getPreviousHash())) {
                return false;
            }

            String expectedHash = calculateHash(current.getCertificateId() + current.getCertificateHash() + current.getPreviousHash() + current.getTimestamp());
            if (!expectedHash.equals(current.getCurrentHash())) {
                return false;
            }
        }

        return true;
    }
}
