package com.sece.block.service;

import com.sece.block.entity.BlockchainBlock;
import com.sece.block.entity.Certificate;
import com.sece.block.entity.Student;
import com.sece.block.repository.CertificateRepository;
import com.sece.block.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Year;
import java.util.List;
import java.util.Optional;

// This service creates a certificate, generates a hash, and stores the certificate record.
@Service
public class CertificateService {

    private final StudentRepository studentRepository;
    private final CertificateRepository certificateRepository;
    private final BlockchainService blockchainService;

    public CertificateService(StudentRepository studentRepository, CertificateRepository certificateRepository,
                              BlockchainService blockchainService) {
        this.studentRepository = studentRepository;
        this.certificateRepository = certificateRepository;
        this.blockchainService = blockchainService;
    }

    public List<Certificate> getAllCertificates() {
        return certificateRepository.findAll();
    }

    public Optional<Certificate> getCertificateById(Long id) {
        return certificateRepository.findById(id);
    }

    public Optional<Certificate> getCertificateByCertificateId(String certificateId) {
        return certificateRepository.findByCertificateId(certificateId);
    }

    public List<Certificate> getCertificatesByStudentId(Long studentId) {
        return certificateRepository.findByStudentId(studentId);
    }

    public Certificate issueCertificate(Long studentId, String courseName, String certificateType, String issueDate) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found."));

        if (courseName == null || courseName.trim().isEmpty()) {
            throw new RuntimeException("Course name is required.");
        }

        if (certificateType == null || certificateType.trim().isEmpty()) {
            throw new RuntimeException("Certificate type is required.");
        }

        if (issueDate == null || issueDate.trim().isEmpty()) {
            throw new RuntimeException("Issue date is required.");
        }

        String certificateId = generateCertificateId();
        String dataToHash = student.getName() + "|" + student.getRegisterNumber() + "|" + courseName + "|"
                + certificateType + "|" + issueDate;

        String hashValue = calculateSha256(dataToHash);

        Certificate certificate = new Certificate();
        certificate.setCertificateId(certificateId);
        certificate.setStudentId(student.getId());
        certificate.setCourseName(courseName);
        certificate.setCertificateType(certificateType);
        certificate.setIssueDate(issueDate);
        certificate.setCertificateHash(hashValue);
        certificate.setBlockchainHash(null);
        certificate.setBlockIndex(null);
        certificate.setStatus("VALID");

        Certificate savedCertificate = certificateRepository.save(certificate);

        List<BlockchainBlock> existingBlocks = blockchainService.getBlockchain();
        if (existingBlocks == null || existingBlocks.isEmpty()) {
            blockchainService.createGenesisBlock();
        }

        blockchainService.addCertificateBlock(savedCertificate);
        return certificateRepository.findById(savedCertificate.getId())
                .orElse(savedCertificate);
    }

    public String generateCertificateId() {
        long totalCertificates = certificateRepository.count();
        String year = String.valueOf(Year.now().getValue());
        String sequence = String.format("%04d", totalCertificates + 1);
        return "CERT-" + year + "-" + sequence;
    }

    public String calculateSha256(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(text.getBytes(StandardCharsets.UTF_8));

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
}
