package com.sece.block.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// This class stores one block in the simple blockchain ledger for certificate verification.
@Entity
@Table(name = "blockchain_block")
public class BlockchainBlock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer blockIndex;

    @Column(nullable = false)
    private String timestamp;

    @Column(nullable = false)
    private String certificateId;

    @Column(nullable = false)
    private String certificateHash;

    @Column(nullable = false)
    private String previousHash;

    @Column(nullable = false)
    private String currentHash;

    public BlockchainBlock() {
    }

    public BlockchainBlock(Integer blockIndex, String timestamp, String certificateId,
                          String certificateHash, String previousHash, String currentHash) {
        this.blockIndex = blockIndex;
        this.timestamp = timestamp;
        this.certificateId = certificateId;
        this.certificateHash = certificateHash;
        this.previousHash = previousHash;
        this.currentHash = currentHash;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getBlockIndex() {
        return blockIndex;
    }

    public void setBlockIndex(Integer blockIndex) {
        this.blockIndex = blockIndex;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getCertificateId() {
        return certificateId;
    }

    public void setCertificateId(String certificateId) {
        this.certificateId = certificateId;
    }

    public String getCertificateHash() {
        return certificateHash;
    }

    public void setCertificateHash(String certificateHash) {
        this.certificateHash = certificateHash;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    public String getCurrentHash() {
        return currentHash;
    }

    public void setCurrentHash(String currentHash) {
        this.currentHash = currentHash;
    }
}
