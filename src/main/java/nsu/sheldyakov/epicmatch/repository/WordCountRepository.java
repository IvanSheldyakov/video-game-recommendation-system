package nsu.sheldyakov.epicmatch.repository;

import java.util.List;
import nsu.sheldyakov.epicmatch.domain.WordCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface WordCountRepository extends JpaRepository<WordCount, WordCount.WordCountId> {

  @Query(
      value = """
            select word from word_count limit :limit
            """,
      nativeQuery = true)
  List<String> findLimitedNumberOfWords(@Param("limit") int limit);

  @Query(
      value =
          """
                select wc from WordCount wc
                join fetch wc.game
                where wc.id.word = :word
            """)
  List<WordCount> findByWord(@Param("word") String word);

  @Query(
      value =
          """
            delete from WordCount wc where wc.id.word in (:words)
            """)
  @Modifying
  @Transactional
  void deleteWhereWordIn(@Param("words") List<String> words);
}
