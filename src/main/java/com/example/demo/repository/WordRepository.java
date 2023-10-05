package com.example.demo.repository;

import com.example.demo.domain.Type;
import com.example.demo.domain.Word;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WordRepository extends JpaRepository<Word, Long> {

  List<Word> findWordsByType(Type type);
}
