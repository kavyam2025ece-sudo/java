package com.sece.block.repository;

import com.sece.block.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// This repository handles all database actions for students.
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByRegisterNumber(String registerNumber);

    Optional<Student> findByEmail(String email);
}
