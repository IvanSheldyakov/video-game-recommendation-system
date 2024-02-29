package nsu.sheldyakov.epicmatch.repository;

import java.util.List;
import nsu.sheldyakov.epicmatch.domain.KeyWordCandidate;
import nsu.sheldyakov.epicmatch.domain.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KeyWordCandidateRepository extends JpaRepository<KeyWordCandidate, String> {

  List<KeyWordCandidate> findByType(Type type);
}
