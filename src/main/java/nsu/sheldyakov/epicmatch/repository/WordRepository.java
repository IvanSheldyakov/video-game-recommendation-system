package nsu.sheldyakov.epicmatch.repository;

import nsu.sheldyakov.epicmatch.domain.Type;
import nsu.sheldyakov.epicmatch.domain.Word;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WordRepository extends JpaRepository<Word, Long> {

  List<Word> findWordsByType(Type type);
}
