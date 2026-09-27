package com.sece.block.controller;

import com.sece.block.entity.Student;
import com.sece.block.entity.Certificate;
import com.sece.block.service.BlockchainService;
import com.sece.block.service.CertificateService;
import com.sece.block.service.PdfService;
import com.sece.block.service.QrCodeService;
import com.sece.block.service.StudentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.time.YearMonth;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

// This controller handles admin pages for student management.
@Controller
@org.springframework.web.bind.annotation.RequestMapping("/admin")
public class AdminController {

    private final StudentService studentService;
    private final CertificateService certificateService;
    private final BlockchainService blockchainService;
    private final QrCodeService qrCodeService;
    private final PdfService pdfService;

    public AdminController(StudentService studentService, CertificateService certificateService,
                           BlockchainService blockchainService, QrCodeService qrCodeService,
                           PdfService pdfService) {
        this.studentService = studentService;
        this.certificateService = certificateService;
        this.blockchainService = blockchainService;
        this.qrCodeService = qrCodeService;
        this.pdfService = pdfService;
    }

    @GetMapping
    public String adminDashboard(Model model) {
        List<Certificate> certificates = certificateService.getAllCertificates();
        String currentMonth = YearMonth.now().toString();

        model.addAttribute("totalStudents", studentService.getAllStudents().size());
        model.addAttribute("totalCertificates", certificates.size());
        model.addAttribute("verifiedCertificates", certificates.stream()
            .filter(certificate -> "VALID".equalsIgnoreCase(certificate.getStatus()))
            .count());
        model.addAttribute("recentCertificates", certificates.stream()
            .filter(certificate -> certificate.getIssueDate() != null
                && certificate.getIssueDate().startsWith(currentMonth))
            .count());
        return "admin-dashboard";
    }

    @GetMapping("/certificates")
    public String viewCertificates(Model model) {
        model.addAttribute("certificates", certificateService.getAllCertificates());
        return "admin-certificates";
    }

    @GetMapping("/blockchain")
    public String viewBlockchain(Model model) {
        model.addAttribute("blocks", blockchainService.getBlockchain());
        model.addAttribute("blockchainValid", blockchainService.verifyBlockchain());
        return "blockchain";
    }

    @PostMapping("/certificates/issue")
    public String issueCertificate(@RequestParam String registerNumber,
                                   @RequestParam String courseName,
                                   @RequestParam String certificateType,
                                   @RequestParam String issueDate,
                                   HttpServletRequest request,
                                   RedirectAttributes redirectAttributes) {
        try {
            Certificate certificate = certificateService.issueCertificate(
                    registerNumber, courseName, certificateType, issueDate);
            String verificationUrl = ServletUriComponentsBuilder.fromRequestUri(request)
                    .replacePath("/verify/" + certificate.getCertificateId())
                    .replaceQuery(null)
                    .build()
                    .toUriString();
            qrCodeService.generateQrCode(verificationUrl, certificate.getCertificateId());
            studentService.getStudentById(certificate.getStudentId()).ifPresent(student ->
                    pdfService.generateCertificatePdf(student, certificate));
            redirectAttributes.addFlashAttribute("successMessage",
                    "Certificate " + certificate.getCertificateId() + " issued successfully.");
            return "redirect:/admin";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/issue-certificate";
        }
    }

    @GetMapping("/students")
    public String viewStudents(Model model) {
        List<Student> students = studentService.getAllStudents();
        model.addAttribute("students", students);
        return "admin-students";
    }

    @GetMapping("/students/add")
    public String showAddStudentForm(Model model) {
        model.addAttribute("student", new Student());
        return "admin-student-form";
    }

    @PostMapping("/students/save")
    public String saveStudent(@ModelAttribute("student") Student student) {
        try {
            studentService.saveStudent(student);
            return "redirect:/admin/students";
        } catch (RuntimeException e) {
            return "redirect:/admin?error=" + e.getMessage();
        }
    }

    @GetMapping("/students/edit/{id}")
    public String editStudent(@PathVariable Long id, Model model) {
        Student student = studentService.getStudentById(id)
                .orElseThrow(() -> new RuntimeException("Student not found."));
        model.addAttribute("student", student);
        return "admin-student-form";
    }

    @GetMapping("/students/delete/{id}")
    public String deleteStudent(@PathVariable Long id) {
        try {
            studentService.deleteStudent(id);
            return "redirect:/admin/students";
        } catch (RuntimeException e) {
            return "redirect:/admin?error=" + e.getMessage();
        }
    }

    @GetMapping("/students/search")
    public String searchStudents(@RequestParam String keyword, Model model) {
        List<Student> students = studentService.searchStudents(keyword);
        model.addAttribute("students", students);
        return "admin-students";
    }
}
