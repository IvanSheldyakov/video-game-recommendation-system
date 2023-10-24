package com.example.demo.repository;

import com.example.demo.domain.Game;
import com.example.demo.model.SearchCriteria;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends JpaRepository<Game, Integer> {

  Game save(Game game);

  @Query(
      """
    select distinct g from Game g
    join g.platforms pl
    join g.genres ge
    join g.publisher pub
    join g.rating r
    where
    (:#{#criteria.platform} is null or :#{#criteria.platform} = '' or pl.platform = :#{#criteria.platform}) and
    (:#{#criteria.minScore} is null or g.score >= :#{#criteria.minScore}) and
    (:#{#criteria.genre} is null or :#{#criteria.genre} = '' or ge.genre = :#{#criteria.genre}) and
    (:#{#criteria.publisher} is null or :#{#criteria.publisher} = '' or pub.publisher = :#{#criteria.publisher}) and
    (:#{#criteria.rating} is null or :#{#criteria.rating} = '' or r.rating = :#{#criteria.rating}) and
    (:#{#criteria.releaseStartDate} is null or g.releaseDate >= :#{#criteria.releaseStartDate}) and
    (:#{#criteria.releaseEndDate} is null or g.releaseDate <= :#{#criteria.releaseEndDate})
    """)
  Page<Game> findGameInfoByFilter(@Param("criteria") SearchCriteria criteria, Pageable pageable);

  @Query(
      """
    select distinct g from Game g
    join fetch g.platforms pl
    join fetch g.genres ge
    join fetch g.publisher pub
    join fetch g.rating r
    where
    (:#{#criteria.platform} is null or :#{#criteria.platform} = '' or pl.platform = :#{#criteria.platform}) and
    (:#{#criteria.minScore} is null or g.score >= :#{#criteria.minScore}) and
    (:#{#criteria.genre} is null or :#{#criteria.genre} = '' or ge.genre = :#{#criteria.genre}) and
    (:#{#criteria.publisher} is null or :#{#criteria.publisher} = '' or pub.publisher = :#{#criteria.publisher}) and
    (:#{#criteria.rating} is null or :#{#criteria.rating} = '' or r.rating = :#{#criteria.rating}) and
    (:#{#criteria.releaseStartDate} is null or g.releaseDate >= :#{#criteria.releaseStartDate}) and
    (:#{#criteria.releaseEndDate} is null or g.releaseDate <= :#{#criteria.releaseEndDate})
    """)
  List<Game> findDetailedGameInfoByFilter(
      @Param("criteria") SearchCriteria criteria, Pageable pageable);
}
