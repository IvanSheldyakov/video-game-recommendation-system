package com.example.demo.repository;

import com.example.demo.domain.WordCount;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WordCountRepository extends JpaRepository<WordCount, Long> {

  boolean existsByWordAndTypeName(String word, String typeName);

  Optional<WordCount> findByWordAndTypeName(String word, String typeName);

  List<WordCount> findAllByTypeNameOrderByCountAsc(String typeName);
}
