package nsu.sheldyakov.epicmatch.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import nsu.sheldyakov.epicmatch.domain.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends JpaRepository<Game, Integer> {

  Game save(Game game);

  @Query(
      value =
          "SELECT g.* FROM game_info g "
              + "INNER JOIN game_platform gp ON g.id = gp.game_id "
              + "INNER JOIN platform pl ON gp.platform = pl.name "
              + "INNER JOIN genre ge ON g.genre = ge.name "
              + "INNER JOIN publisher pub ON g.publisher = pub.name "
              + "INNER JOIN age_rating r ON g.rating = r.name "
              + "WHERE "
              + "(?1 IS NULL OR ?1 = '' OR pl.name = ?1) AND "
              + "(?2 IS NULL OR g.score >= ?2) AND "
              + "(?3 IS NULL OR ?3 = '' OR ge.name = ?3) AND "
              + "(?4 IS NULL OR ?4 = '' OR pub.name = ?4) AND "
              + "(?5 IS NULL OR ?5 = '' OR r.name = ?5) AND "
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
          "SELECT count(*) FROM game_info g "
              + "INNER JOIN game_platform gp ON g.id = gp.game_id "
              + "INNER JOIN platform pl ON gp.platform = pl.name "
              + "INNER JOIN genre ge ON g.genre = ge.name "
              + "INNER JOIN publisher pub ON g.publisher = pub.name "
              + "INNER JOIN age_rating r ON g.rating = r.name "
              + "WHERE "
              + "(?1 IS NULL OR ?1 = '' OR pl.name = ?1) AND "
              + "(?2 IS NULL OR g.score >= ?2) AND "
              + "(?3 IS NULL OR ?3 = '' OR ge.name = ?3) AND "
              + "(?4 IS NULL OR ?4 = '' OR pub.name = ?4) AND "
              + "(?5 IS NULL OR ?5 = '' OR r.name = ?5) AND "
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
            select * from game_info where vector is null limit 1
            """,
      nativeQuery = true)
  Optional<Game> findNotAnalyzedGame();

  @Query(
      value =
          """
                    select count(*) from game_info where vector is not null

                    """,
      nativeQuery = true)
  long countAnalyzedGames();
}
