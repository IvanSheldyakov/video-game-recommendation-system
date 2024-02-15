package nsu.sheldyakov.epicmatch.repository;

import java.time.LocalDate;
import java.util.List;
import nsu.sheldyakov.epicmatch.domain.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends JpaRepository<Game, Integer> {

  Game save(Game game);

  @Query(
      value =
          "SELECT g.* FROM game g "
              + "INNER JOIN game_platform gp ON g.id = gp.game_id "
              + "INNER JOIN platform pl ON gp.platform_id = pl.id "
              + "INNER JOIN game_genre gg ON g.id = gg.game_id "
              + "INNER JOIN genre ge ON gg.genre_id = ge.id "
              + "INNER JOIN publisher pub ON g.publisher_id = pub.id "
              + "INNER JOIN rating r ON g.rating_id = r.id "
              + "WHERE "
              + "(?1 IS NULL OR ?1 = '' OR pl.platform = ?1) AND "
              + "(?2 IS NULL OR g.score >= ?2) AND "
              + "(?3 IS NULL OR ?3 = '' OR ge.genre = ?3) AND "
              + "(?4 IS NULL OR ?4 = '' OR pub.publisher = ?4) AND "
              + "(?5 IS NULL OR ?5 = '' OR r.rating = ?5) AND "
              + "(?6 IS NULL OR g.release_date >= ?6) AND "
              + "(?7 IS NULL OR g.release_date <= ?7) "
              + "ORDER BY calculate_cosine_similarity(?8, g.vector) DESC LIMIT ?9 OFFSET ?10",
      nativeQuery = true)
  List<Game> findGameInfoByFilter(
      String platform,
      Integer minScore,
      String genre,
      String publisher,
      String rating,
      LocalDate releaseStartDate,
      LocalDate releaseEndDate,
      String fixedVector,
      int limit,
      int offset);

  @Query(
      value =
          "SELECT count(*) FROM game g "
              + "INNER JOIN game_platform gp ON g.id = gp.game_id "
              + "INNER JOIN platform pl ON gp.platform_id = pl.id "
              + "INNER JOIN game_genre gg ON g.id = gg.game_id "
              + "INNER JOIN genre ge ON gg.genre_id = ge.id "
              + "INNER JOIN publisher pub ON g.publisher_id = pub.id "
              + "INNER JOIN rating r ON g.rating_id = r.id "
              + "WHERE "
              + "(?1 IS NULL OR ?1 = '' OR pl.platform = ?1) AND "
              + "(?2 IS NULL OR g.score >= ?2) AND "
              + "(?3 IS NULL OR ?3 = '' OR ge.genre = ?3) AND "
              + "(?4 IS NULL OR ?4 = '' OR pub.publisher = ?4) AND "
              + "(?5 IS NULL OR ?5 = '' OR r.rating = ?5) AND "
              + "(?6 IS NULL OR g.release_date >= ?6) AND "
              + "(?7 IS NULL OR g.release_date <= ?7) ",
      nativeQuery = true)
  long countGameInfoByFilter(
      String platform,
      Integer minScore,
      String genre,
      String publisher,
      String rating,
      LocalDate releaseStartDate,
      LocalDate releaseEndDate);

  @Query(
      value =
          """
            select * from game_info where vector is null limit 4
            """,
      nativeQuery = true)
  List<Game> findNotAnalyzedGames();
}
