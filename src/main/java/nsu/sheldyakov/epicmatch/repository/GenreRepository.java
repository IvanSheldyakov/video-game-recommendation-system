package nsu.sheldyakov.epicmatch.repository;

import java.util.Optional;
import nsu.sheldyakov.epicmatch.domain.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreRepository extends JpaRepository<Genre, String> {

  Optional<Genre> findByName(String string);
}
