package com.sece.block.controller;

import com.sece.block.entity.Student;
import com.sece.block.service.CertificateService;
import com.sece.block.service.StudentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

// This controller loads the home, login, register, and dashboard pages for the application.
@Controller
public class HomeController {

    private final StudentService studentService;
    private final CertificateService certificateService;

    public HomeController(StudentService studentService, CertificateService certificateService) {
        this.studentService = studentService;
        this.certificateService = certificateService;
    }

    @GetMapping("/")
    public String homePage() {
        return "index";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @GetMapping("/issue-certificate")
    public String issueCertificatePage() {
        return "issue-certificate";
    }

    @GetMapping("/verify-certificate")
    public String verifyCertificatePage(@RequestParam(required = false) String certificateId, Model model) {
        model.addAttribute("certificateId", certificateId == null ? "" : certificateId);
        return "verify-certificate";
    }

    @GetMapping("/verify/{certificateId}")
    public String verifyCertificateLink(@org.springframework.web.bind.annotation.PathVariable String certificateId,
                                        Model model) {
        model.addAttribute("certificateId", certificateId);
        return "verify-certificate";
    }

    @GetMapping("/student-dashboard")
    public String studentDashboard(Model model, Principal principal) {
        String username = principal != null ? principal.getName() : "student";
        String studentName = "Student User";

        List<Student> students = studentService.getAllStudents();
        if (!students.isEmpty()) {
            Student student = students.get(0);
            studentName = student.getName() != null ? student.getName() : studentName;
            model.addAttribute("studentId", student.getId());
            model.addAttribute("studentRegisterNumber", student.getRegisterNumber());
            model.addAttribute("studentEmail", student.getEmail());
            model.addAttribute("studentCourse", student.getCourse());
            model.addAttribute("studentDepartment", student.getDepartment());
            model.addAttribute("certificates", certificateService.getCertificatesByStudentId(student.getId()));
        } else {
            model.addAttribute("studentId", "N/A");
            model.addAttribute("studentRegisterNumber", "N/A");
            model.addAttribute("studentEmail", username + "@example.com");
            model.addAttribute("studentCourse", "Computer Science");
            model.addAttribute("studentDepartment", "Engineering");
            model.addAttribute("certificates", List.of());
        }

        model.addAttribute("studentName", studentName);
        model.addAttribute("username", username);
        return "student-dashboard";
    }

    @PostMapping("/student-dashboard/upload")
    public String uploadCertificate(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        if (file == null || file.isEmpty()) {
            redirectAttributes.addFlashAttribute("uploadError", "Please select a certificate file.");
            return "redirect:/student-dashboard";
        }

        try {
            String uploadDir = "src/main/resources/static/uploads";
            Path directory = Paths.get(uploadDir);
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }

            String originalName = file.getOriginalFilename() == null ? "certificate" : file.getOriginalFilename();
            String safeFileName = System.currentTimeMillis() + "-" + originalName.replaceAll("[^a-zA-Z0-9._-]", "_");
            Path savedPath = directory.resolve(safeFileName);
            Files.copy(file.getInputStream(), savedPath, StandardCopyOption.REPLACE_EXISTING);

            redirectAttributes.addFlashAttribute("uploadSuccess", "Certificate uploaded successfully: " + safeFileName);
            return "redirect:/student-dashboard";
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("uploadError", "Upload failed. Please try again.");
            return "redirect:/student-dashboard";
        }
    }
}
