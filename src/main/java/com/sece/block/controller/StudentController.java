package com.sece.block.controller;

import com.sece.block.entity.Student;
import com.sece.block.service.StudentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// This controller exposes simple REST APIs for student data.
@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }

    @GetMapping("/{id}")
    public Student getStudentById(@PathVariable Long id) {
        return studentService.getStudentById(id)
                .orElseThrow(() -> new RuntimeException("Student not found."));
    }

    @PostMapping
    public Student createStudent(@RequestBody Student student) {
        return studentService.saveStudent(student);
    }

    @PutMapping("/{id}")
    public Student updateStudent(@PathVariable Long id, @RequestBody Student studentDetails) {
        Student student = studentService.getStudentById(id)
                .orElseThrow(() -> new RuntimeException("Student not found."));

        student.setName(studentDetails.getName());
        student.setRegisterNumber(studentDetails.getRegisterNumber());
        student.setEmail(studentDetails.getEmail());
        student.setDepartment(studentDetails.getDepartment());
        student.setCourse(studentDetails.getCourse());
        student.setGraduationYear(studentDetails.getGraduationYear());

        return studentService.saveStudent(student);
    }

    @DeleteMapping("/{id}")
    public String deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return "Student deleted successfully.";
    }
}
