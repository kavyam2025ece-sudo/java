package com.sece.block.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// This class stores the certificate information after it is created by the admin.
@Entity
@Table(name = "certificate")
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String certificateId;

    @Column(nullable = false)
    private Long studentId;

    @Column(nullable = false)
    private String courseName;

    @Column(nullable = false)
    private String certificateType;

    @Column(nullable = false)
    private String issueDate;

    @Column(nullable = false)
    private String certificateHash;

    @Column
    private String blockchainHash;

    @Column
    private Integer blockIndex;

    @Column(nullable = false)
    private String status;

    public Certificate() {
    }

    public Certificate(String certificateId, Long studentId, String courseName,
                      String certificateType, String issueDate, String certificateHash,
                      String blockchainHash, Integer blockIndex, String status) {
        this.certificateId = certificateId;
        this.studentId = studentId;
        this.courseName = courseName;
        this.certificateType = certificateType;
        this.issueDate = issueDate;
        this.certificateHash = certificateHash;
        this.blockchainHash = blockchainHash;
        this.blockIndex = blockIndex;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCertificateId() {
        return certificateId;
    }

    public void setCertificateId(String certificateId) {
        this.certificateId = certificateId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCertificateType() {
        return certificateType;
    }

    public void setCertificateType(String certificateType) {
        this.certificateType = certificateType;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    public String getCertificateHash() {
        return certificateHash;
    }

    public void setCertificateHash(String certificateHash) {
        this.certificateHash = certificateHash;
    }

    public String getBlockchainHash() {
        return blockchainHash;
    }

    public void setBlockchainHash(String blockchainHash) {
        this.blockchainHash = blockchainHash;
    }

    public Integer getBlockIndex() {
        return blockIndex;
    }

    public void setBlockIndex(Integer blockIndex) {
        this.blockIndex = blockIndex;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
