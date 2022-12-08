package com.example.demo.repository;

import com.example.demo.domain.Type;
import com.example.demo.domain.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WordRepository extends JpaRepository<Word, Long> {

    List<Word> findWordsByType(Type type);
}
