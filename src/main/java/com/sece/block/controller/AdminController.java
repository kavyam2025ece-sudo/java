package com.sece.block.controller;

import com.sece.block.entity.Student;
import com.sece.block.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

// This controller handles admin pages for student management.
@Controller
public class AdminController {

    @Autowired
    private StudentService studentService;

    @GetMapping("/admin")
    public String adminDashboard() {
        return "admin-dashboard";
    }

    @GetMapping("/admin/students")
    public String viewStudents(Model model) {
        List<Student> students = studentService.getAllStudents();
        model.addAttribute("students", students);
        return "student-dashboard";
    }

    @GetMapping("/admin/students/add")
    public String showAddStudentForm(Model model) {
        model.addAttribute("student", new Student());
        return "admin-dashboard";
    }

    @PostMapping("/admin/students/save")
    public String saveStudent(@ModelAttribute("student") Student student) {
        try {
            studentService.saveStudent(student);
            return "redirect:/admin/students";
        } catch (RuntimeException e) {
            return "redirect:/admin?error=" + e.getMessage();
        }
    }

    @GetMapping("/admin/students/edit/{id}")
    public String editStudent(@PathVariable Long id, Model model) {
        Student student = studentService.getStudentById(id)
                .orElseThrow(() -> new RuntimeException("Student not found."));
        model.addAttribute("student", student);
        return "student-dashboard";
    }

    @GetMapping("/admin/students/delete/{id}")
    public String deleteStudent(@PathVariable Long id) {
        try {
            studentService.deleteStudent(id);
            return "redirect:/admin/students";
        } catch (RuntimeException e) {
            return "redirect:/admin?error=" + e.getMessage();
        }
    }

    @GetMapping("/admin/students/search")
    public String searchStudents(@RequestParam String keyword, Model model) {
        List<Student> students = studentService.searchStudents(keyword);
        model.addAttribute("students", students);
        return "student-dashboard";
    }
}
