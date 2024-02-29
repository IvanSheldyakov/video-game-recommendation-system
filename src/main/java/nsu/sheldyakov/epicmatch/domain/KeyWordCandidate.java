package nsu.sheldyakov.epicmatch.domain;

import java.util.Objects;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

@Entity
@Table(name = "key_word_candidate")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KeyWordCandidate {

  @Id private String word;

  @Column(name = "average_tfidf")
  private Double averageTfidf;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "game_type")
  private Type type;

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
    KeyWordCandidate that = (KeyWordCandidate) o;
    return getWord() != null && Objects.equals(getWord(), that.getWord());
  }

  @Override
  public final int hashCode() {
    return this instanceof HibernateProxy
        ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
        : getClass().hashCode();
  }
}
