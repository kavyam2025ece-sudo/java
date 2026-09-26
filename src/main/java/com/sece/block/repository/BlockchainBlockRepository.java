package com.sece.block.repository;

import com.sece.block.entity.BlockchainBlock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// This repository handles blockchain record storage and lookup.
@Repository
public interface BlockchainBlockRepository extends JpaRepository<BlockchainBlock, Long> {

    Optional<BlockchainBlock> findByCertificateId(String certificateId);

    List<BlockchainBlock> findAllByOrderByBlockIndexAsc();
}
