package nsu.sheldyakov.epicmatch.repository;

import nsu.sheldyakov.epicmatch.domain.Genre;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Integer> {

  Optional<Genre> findByGenre(String genre);
}
