package com.example.demo.repository;

import com.example.demo.domain.BlockWord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlockWordRepository extends JpaRepository<BlockWord, Long> {}
