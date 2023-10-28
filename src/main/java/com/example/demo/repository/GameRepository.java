package com.example.demo.repository;

import com.example.demo.domain.Game;
import com.example.demo.model.SearchCriteria;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends JpaRepository<Game, Integer> {

  Game save(Game game);

  @Query(
      "SELECT g FROM Game g "
          + "JOIN g.platforms pl "
          + "JOIN g.genres ge "
          + "JOIN g.publisher pub "
          + "JOIN g.rating r "
          + "WHERE "
          + "(:#{#criteria.platform} IS NULL OR :#{#criteria.platform} = '' OR pl.platform = :#{#criteria.platform}) AND "
          + "(:#{#criteria.minScore} IS NULL OR g.score >= :#{#criteria.minScore}) AND "
          + "(:#{#criteria.genre} IS NULL OR :#{#criteria.genre} = '' OR ge.genre = :#{#criteria.genre}) AND "
          + "(:#{#criteria.publisher} IS NULL OR :#{#criteria.publisher} = '' OR pub.publisher = :#{#criteria.publisher}) AND "
          + "(:#{#criteria.rating} IS NULL OR :#{#criteria.rating} = '' OR r.rating = :#{#criteria.rating}) AND "
          + "(:#{#criteria.releaseStartDate} IS NULL OR g.releaseDate >= :#{#criteria.releaseStartDate}) AND "
          + "(:#{#criteria.releaseEndDate} IS NULL OR g.releaseDate <= :#{#criteria.releaseEndDate}) "
          + "ORDER BY FUNCTION('calculate_cosine_similarity', :fixedVector, g.vector) DESC")
  List<Game> findGameInfoByFilter(
      @Param("criteria") SearchCriteria criteria,
      @Param("fixedVector") String fixedVector,
      Pageable pageable);

  @Query(
      "SELECT count(*) FROM Game g "
          + "JOIN g.platforms pl "
          + "JOIN g.genres ge "
          + "JOIN g.publisher pub "
          + "JOIN g.rating r "
          + "WHERE "
          + "(:#{#criteria.platform} IS NULL OR :#{#criteria.platform} = '' OR pl.platform = :#{#criteria.platform}) AND "
          + "(:#{#criteria.minScore} IS NULL OR g.score >= :#{#criteria.minScore}) AND "
          + "(:#{#criteria.genre} IS NULL OR :#{#criteria.genre} = '' OR ge.genre = :#{#criteria.genre}) AND "
          + "(:#{#criteria.publisher} IS NULL OR :#{#criteria.publisher} = '' OR pub.publisher = :#{#criteria.publisher}) AND "
          + "(:#{#criteria.rating} IS NULL OR :#{#criteria.rating} = '' OR r.rating = :#{#criteria.rating}) AND "
          + "(:#{#criteria.releaseStartDate} IS NULL OR g.releaseDate >= :#{#criteria.releaseStartDate}) AND "
          + "(:#{#criteria.releaseEndDate} IS NULL OR g.releaseDate <= :#{#criteria.releaseEndDate})")
  Long countGames(@Param("criteria") SearchCriteria criteria);
}
