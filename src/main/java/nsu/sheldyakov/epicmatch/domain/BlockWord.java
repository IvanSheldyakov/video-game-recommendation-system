package nsu.sheldyakov.epicmatch.domain;

import java.util.Objects;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "block_word")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BlockWord {

  @Id private String word;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof BlockWord)) return false;
    BlockWord blockWord = (BlockWord) o;
    return Objects.equals(getWord(), blockWord.getWord());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getWord());
  }
}
