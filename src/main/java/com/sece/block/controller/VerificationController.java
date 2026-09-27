package com.sece.block.controller;

import com.sece.block.entity.BlockchainBlock;
import com.sece.block.repository.BlockchainBlockRepository;
import com.sece.block.service.BlockchainService;
import com.sece.block.service.QrCodeService;
import com.sece.block.service.VerificationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

// This controller exposes the public verification page and API for checking certificate authenticity.
@RestController
@RequestMapping("/api")
public class VerificationController {

    private final VerificationService verificationService;
    private final BlockchainBlockRepository blockchainBlockRepository;
    private final BlockchainService blockchainService;
    private final QrCodeService qrCodeService;

    public VerificationController(VerificationService verificationService,
                                   BlockchainBlockRepository blockchainBlockRepository,
                                   BlockchainService blockchainService,
                                   QrCodeService qrCodeService) {
        this.verificationService = verificationService;
        this.blockchainBlockRepository = blockchainBlockRepository;
        this.blockchainService = blockchainService;
        this.qrCodeService = qrCodeService;
    }

    @GetMapping("/verify/{certificateId}")
    public Map<String, Object> verifyCertificate(@PathVariable String certificateId) {
        return verifyCertificateData(certificateId);
    }

    @GetMapping("/verify")
    public Map<String, Object> verifyCertificateByRequest(@RequestParam String certificateId) {
        return verifyCertificateData(certificateId);
    }

    @GetMapping("/verify-api")
    public Map<String, Object> verifyCertificateByApi(@RequestParam String certificateId) {
        return verifyCertificateData(certificateId);
    }

    @GetMapping(value = "/verify/{certificateId}/qr", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] getVerificationQrCode(@PathVariable String certificateId, HttpServletRequest request) {
        String verificationUrl = ServletUriComponentsBuilder.fromRequestUri(request)
                .replacePath("/verify/" + certificateId)
                .replaceQuery(null)
                .build()
                .toUriString();
        return qrCodeService.generateQrCodeBytes(verificationUrl);
    }

    private Map<String, Object> verifyCertificateData(String certificateId) {
        VerificationService.VerificationResult result = verificationService.verifyCertificate(certificateId);
        Map<String, Object> response = new LinkedHashMap<>();

        if (result.getCertificate() == null) {
            response.put("valid", false);
            response.put("message", result.getMessage());
            return response;
        }

        response.put("valid", result.isValid());
        response.put("message", result.getMessage());
        response.put("certificateId", result.getCertificate().getCertificateId());
        response.put("studentName", result.getStudent().getName());
        response.put("registerNumber", result.getStudent().getRegisterNumber());
        response.put("course", result.getCertificate().getCourseName());
        response.put("certificateType", result.getCertificate().getCertificateType());
        response.put("issueDate", result.getCertificate().getIssueDate());
        response.put("certificateHash", result.getCertificate().getCertificateHash());
        response.put("blockchainHash", result.getCertificate().getBlockchainHash());
        response.put("blockIndex", result.getCertificate().getBlockIndex());
        response.put("verificationStatus", result.isValid() ? "Verified" : "Invalid");

        return response;
    }

    @GetMapping("/blockchain")
    public Iterable<BlockchainBlock> getAllBlocks() {
        return blockchainBlockRepository.findAllByOrderByBlockIndexAsc();
    }

    @GetMapping("/blockchain/verify")
    public Map<String, Object> verifyBlockchain() {
        boolean valid = blockchainService.verifyBlockchain();
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("valid", valid);
        response.put("message", valid ? "Blockchain is valid." : "Blockchain verification failed.");
        return response;
    }
}
