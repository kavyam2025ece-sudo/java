package com.sece.block.service;

import com.sece.block.entity.BlockchainBlock;
import com.sece.block.entity.Certificate;
import com.sece.block.entity.Student;
import com.sece.block.repository.BlockchainBlockRepository;
import com.sece.block.repository.CertificateRepository;
import com.sece.block.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

// This service checks whether a certificate is valid by comparing the stored hash and blockchain record.
@Service
public class VerificationService {

    private final CertificateRepository certificateRepository;
    private final BlockchainBlockRepository blockchainBlockRepository;
    private final StudentRepository studentRepository;
    private final BlockchainService blockchainService;

    public VerificationService(CertificateRepository certificateRepository,
                               BlockchainBlockRepository blockchainBlockRepository,
                               StudentRepository studentRepository,
                               BlockchainService blockchainService) {
        this.certificateRepository = certificateRepository;
        this.blockchainBlockRepository = blockchainBlockRepository;
        this.studentRepository = studentRepository;
        this.blockchainService = blockchainService;
    }

    public VerificationResult verifyCertificate(String certificateId) {
        Optional<Certificate> certificateOptional = certificateRepository.findByCertificateId(certificateId);
        if (certificateOptional.isEmpty()) {
            return new VerificationResult(false, "Certificate does not exist.", null, null, null, null);
        }

        Certificate certificate = certificateOptional.get();
        Optional<Student> studentOptional = studentRepository.findById(certificate.getStudentId());
        if (studentOptional.isEmpty()) {
            return new VerificationResult(false, "Student record is missing.", null, null, null, null);
        }

        Student student = studentOptional.get();
        Optional<BlockchainBlock> blockOptional = blockchainBlockRepository.findByCertificateId(certificateId);
        if (blockOptional.isEmpty()) {
            return new VerificationResult(false, "Blockchain record not found.", student, certificate, null, null);
        }

        BlockchainBlock block = blockOptional.get();
        String expectedHash = blockchainService.calculateHash(
                certificate.getCertificateId() + certificate.getCertificateHash() + block.getPreviousHash() + block.getTimestamp());

        boolean valid = expectedHash.equals(block.getCurrentHash()) && blockchainService.verifyBlockchain();

        if (!valid) {
            return new VerificationResult(false, "Certificate data was modified or blockchain chain is invalid.", student, certificate, block, "Blockchain verification failed.");
        }

        return new VerificationResult(true, "Certificate Verified Successfully", student, certificate, block, "Valid blockchain chain.");
    }

    public static class VerificationResult {
        private boolean valid;
        private String message;
        private Student student;
        private Certificate certificate;
        private BlockchainBlock blockchainBlock;
        private String extraMessage;

        public VerificationResult(boolean valid, String message, Student student, Certificate certificate,
                                 BlockchainBlock blockchainBlock, String extraMessage) {
            this.valid = valid;
            this.message = message;
            this.student = student;
            this.certificate = certificate;
            this.blockchainBlock = blockchainBlock;
            this.extraMessage = extraMessage;
        }

        public boolean isValid() {
            return valid;
        }

        public String getMessage() {
            return message;
        }

        public Student getStudent() {
            return student;
        }

        public Certificate getCertificate() {
            return certificate;
        }

        public BlockchainBlock getBlockchainBlock() {
            return blockchainBlock;
        }

        public String getExtraMessage() {
            return extraMessage;
        }
    }
}
