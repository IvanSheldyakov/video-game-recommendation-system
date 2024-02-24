package nsu.sheldyakov.epicmatch.domain;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

@Entity
@Table(name = "word_count")
@Getter
@Setter
@NoArgsConstructor
public class WordCount {

  @EmbeddedId private WordCountId id;

  @Column(name = "relative_frequency")
  private Double relativeFrequency;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "game_id", insertable = false, updatable = false)
  private Game game;

  @Embeddable
  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class WordCountId implements Serializable {

    @Column(name = "word")
    private String word;

    @Column(name = "game_id")
    private Integer gameId;
  }

  public WordCount(String word, Double relativeFrequency, Game game) {
    this.relativeFrequency = relativeFrequency;
    this.game = game;
    this.id = new WordCountId(word, game.getId());
  }

  @Override
  public final boolean equals(Object o) {
    if (this == o) return true;
    if (o == null) return false;
    Class<?> oEffectiveClass =
        o instanceof HibernateProxy
            ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass()
            : o.getClass();
    Class<?> thisEffectiveClass =
        this instanceof HibernateProxy
            ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass()
            : this.getClass();
    if (thisEffectiveClass != oEffectiveClass) return false;
    WordCount wordCount = (WordCount) o;
    return getId() != null && Objects.equals(getId(), wordCount.getId());
  }

  @Override
  public final int hashCode() {
    return Objects.hash(id);
  }
}
