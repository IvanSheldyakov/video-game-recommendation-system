package nsu.sheldyakov.epicmatch.domain;

import java.util.Objects;
import javax.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "word_count")
@Getter
@Setter
@NoArgsConstructor
public class WordCount {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private String word;

  private String typeName;

  private long count;

  private int inGames;

  public WordCount(String word, String typeName) {
    this.word = word;
    this.typeName = typeName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof WordCount)) return false;
    WordCount wordCount = (WordCount) o;
    return Objects.equals(getWord(), wordCount.getWord())
        && Objects.equals(getTypeName(), wordCount.getTypeName());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getWord(), getTypeName());
  }
}
