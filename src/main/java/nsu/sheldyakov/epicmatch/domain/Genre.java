package nsu.sheldyakov.epicmatch.domain;

import java.util.Objects;
import java.util.Set;
import javax.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "genre")
@Getter
@Setter
@NoArgsConstructor
public class Genre {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String genre;

  @ManyToMany private Set<Game> games;

  public Genre(String genre) {
    this.genre = genre;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Genre)) return false;
    Genre genre1 = (Genre) o;
    return Objects.equals(getGenre(), genre1.getGenre());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getGenre());
  }
}
