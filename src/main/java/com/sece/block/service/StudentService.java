package com.sece.block.service;

import com.sece.block.entity.Student;
import com.sece.block.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// This service contains the student CRUD logic used by the admin pages and APIs.
@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Optional<Student> getStudentById(Long id) {
        return studentRepository.findById(id);
    }

    public Student saveStudent(Student student) {
        Optional<Student> existingStudent = studentRepository.findByRegisterNumber(student.getRegisterNumber());

        if (existingStudent.isPresent() && (student.getId() == null || !existingStudent.get().getId().equals(student.getId()))) {
            throw new RuntimeException("Student with this register number already exists.");
        }

        Optional<Student> existingEmail = studentRepository.findByEmail(student.getEmail());
        if (existingEmail.isPresent() && (student.getId() == null || !existingEmail.get().getId().equals(student.getId()))) {
            throw new RuntimeException("Student with this email already exists.");
        }

        return studentRepository.save(student);
    }

    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new RuntimeException("Student not found.");
        }

        studentRepository.deleteById(id);
    }

    public List<Student> searchStudents(String keyword) {
        List<Student> students = studentRepository.findAll();
        List<Student> result = new java.util.ArrayList<>();

        for (Student student : students) {
            String text = (student.getName() + " " + student.getRegisterNumber() + " " + student.getEmail() + " " + student.getDepartment() + " " + student.getCourse()).toLowerCase();
            if (text.contains(keyword.toLowerCase())) {
                result.add(student);
            }
        }

        return result;
    }
}
