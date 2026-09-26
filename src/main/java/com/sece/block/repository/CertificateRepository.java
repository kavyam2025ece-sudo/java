package com.sece.block.repository;

import com.sece.block.entity.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// This repository handles all database actions for certificates.
@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {

    Optional<Certificate> findByCertificateId(String certificateId);

    List<Certificate> findByStudentId(Long studentId);

    List<Certificate> findByStatus(String status);
}
