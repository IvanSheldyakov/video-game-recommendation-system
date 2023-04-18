package com.example.demo.repository;

import com.example.demo.domain.WordCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WordCountRepository extends JpaRepository<WordCount, Long> {

    boolean existsByWordAndTypeName(String word, String typeName);

    WordCount findByWordAndTypeName(String word, String typeName);

    List<WordCount> findAllByTypeNameOrderByCountAsc(String typeName);
}
