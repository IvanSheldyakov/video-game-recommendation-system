package com.example.demo.repository;

import com.example.demo.domain.Rating;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Integer> {

  Optional<Rating> findByRating(String rating);
}
