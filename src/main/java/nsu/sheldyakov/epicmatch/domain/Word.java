package nsu.sheldyakov.epicmatch.domain;

import javax.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "words")
@Getter
@Setter
@NoArgsConstructor
public class Word {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String word;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "type_id")
  private Type type;

  public Word(String word, Type type) {
    this.word = word;
    this.type = type;
  }

  @Override
  public String toString() {
    return "Word{" + ", word='" + word + '\'' + ", type=" + type.getTypeName() + '}';
  }
}
