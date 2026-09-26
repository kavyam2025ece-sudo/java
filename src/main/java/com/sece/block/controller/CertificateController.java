package com.sece.block.controller;

import com.sece.block.entity.Certificate;
import com.sece.block.entity.Student;
import com.sece.block.repository.StudentRepository;
import com.sece.block.service.CertificateService;
import com.sece.block.service.PdfService;
import com.sece.block.service.QrCodeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// This controller handles certificate issuing and certificate APIs.
@RestController
@RequestMapping("/api/certificates")
public class CertificateController {

    private final CertificateService certificateService;
    private final QrCodeService qrCodeService;
    private final PdfService pdfService;
    private final StudentRepository studentRepository;

    public CertificateController(CertificateService certificateService, QrCodeService qrCodeService,
                                 PdfService pdfService, StudentRepository studentRepository) {
        this.certificateService = certificateService;
        this.qrCodeService = qrCodeService;
        this.pdfService = pdfService;
        this.studentRepository = studentRepository;
    }

    @GetMapping
    public List<Certificate> getAllCertificates() {
        return certificateService.getAllCertificates();
    }

    @GetMapping("/{id}")
    public Certificate getCertificateById(@PathVariable Long id) {
        return certificateService.getCertificateById(id)
                .orElseThrow(() -> new RuntimeException("Certificate not found."));
    }

    @PostMapping("/issue")
    public Certificate issueCertificate(
            @RequestParam Long studentId,
            @RequestParam String courseName,
            @RequestParam String certificateType,
            @RequestParam String issueDate) {

        Certificate certificate = certificateService.issueCertificate(studentId, courseName, certificateType, issueDate);

        String verificationUrl = "http://localhost:8080/verify/" + certificate.getCertificateId();
        String qrPath = qrCodeService.generateQrCode(verificationUrl, certificate.getCertificateId());

        Student student = studentRepository.findById(studentId).orElse(null);
        if (student != null) {
            String pdfPath = pdfService.generateCertificatePdf(student, certificate);
            System.out.println("QR generated for student: " + student.getName() + " -> " + qrPath);
            System.out.println("PDF generated: " + pdfPath);
        }

        return certificate;
    }
}
