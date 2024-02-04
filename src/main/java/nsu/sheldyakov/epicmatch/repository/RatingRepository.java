package nsu.sheldyakov.epicmatch.repository;

import java.util.Optional;
import nsu.sheldyakov.epicmatch.domain.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Integer> {

  Optional<Rating> findByRating(String rating);
}
