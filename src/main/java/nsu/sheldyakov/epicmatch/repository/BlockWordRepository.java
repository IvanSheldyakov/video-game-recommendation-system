package nsu.sheldyakov.epicmatch.repository;

import nsu.sheldyakov.epicmatch.domain.BlockWord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlockWordRepository extends JpaRepository<BlockWord, Long> {}
