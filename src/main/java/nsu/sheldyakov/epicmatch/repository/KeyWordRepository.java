package nsu.sheldyakov.epicmatch.repository;

import java.util.List;
import nsu.sheldyakov.epicmatch.domain.KeyWord;
import nsu.sheldyakov.epicmatch.domain.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KeyWordRepository extends JpaRepository<KeyWord, String> {

  List<KeyWord> findWordsByType(Type type);
}
