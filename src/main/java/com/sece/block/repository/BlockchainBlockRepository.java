package com.sece.block.repository;

import com.sece.block.entity.BlockchainBlock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

// This repository handles blockchain record storage and lookup.
public interface BlockchainBlockRepository extends JpaRepository<BlockchainBlock, Long> {

    Optional<BlockchainBlock> findByCertificateId(String certificateId);

    List<BlockchainBlock> findAllByOrderByBlockIndexAsc();
}
