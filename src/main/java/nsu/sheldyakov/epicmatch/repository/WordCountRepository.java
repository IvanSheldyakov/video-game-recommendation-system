package nsu.sheldyakov.epicmatch.repository;

import nsu.sheldyakov.epicmatch.domain.WordCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WordCountRepository extends JpaRepository<WordCount, String> {}
