package nsu.sheldyakov.epicmatch.domain;

import com.vladmihalcea.hibernate.type.array.DoubleArrayType;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.TypeDef;

@Entity
@Table(name = "game")
@Getter
@Setter
@TypeDef(name = "double-array", typeClass = DoubleArrayType.class)
public class Game {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String name;

  private Integer score;

  @Column(name = "release_date", columnDefinition = "DATE")
  private LocalDate releaseDate;

  @Column(name = "summary", columnDefinition = "text")
  private String summary;

  @org.hibernate.annotations.Type(type = "double-array")
  @Column(name = "vector", columnDefinition = "double precision[]")
  private Double[] vector;

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "type_id")
  private Type type;

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "rating_id")
  private Rating rating;

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "publisher_id")
  private Publisher publisher;

  @ManyToMany(cascade = CascadeType.ALL)
  @JoinTable(
      name = "game_genre",
      joinColumns = @JoinColumn(name = "game_id"),
      inverseJoinColumns = @JoinColumn(name = "genre_id"))
  private Set<Genre> genres = new HashSet<>();

  @ManyToMany(cascade = CascadeType.ALL)
  @JoinTable(
      name = "game_platform",
      joinColumns = @JoinColumn(name = "game_id"),
      inverseJoinColumns = @JoinColumn(name = "platform_id"))
  private Set<Platform> platforms = new HashSet<>();

  @Override
  public String toString() {
    return "Game{" + "name='" + name + '\'' + '}';
  }
}
