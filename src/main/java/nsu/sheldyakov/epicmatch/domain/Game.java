package nsu.sheldyakov.epicmatch.domain;

import com.vladmihalcea.hibernate.type.array.DoubleArrayType;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.TypeDef;
import org.hibernate.proxy.HibernateProxy;

@Entity
@Table(name = "game_info")
@Getter
@Setter
@TypeDef(name = "double-array", typeClass = DoubleArrayType.class)
public class Game {

  @Id private Integer id;

  private String name;

  private Integer score;

  @Column(name = "release_date", columnDefinition = "DATE")
  private LocalDate releaseDate;

  @Column(name = "summary", columnDefinition = "text")
  private String summary;

  @org.hibernate.annotations.Type(type = "double-array")
  @Column(name = "vector", columnDefinition = "double precision[]")
  private Double[] vector;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "rating")
  private Rating rating;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "publisher")
  private Publisher publisher;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "genre")
  private Genre genre;

  @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinTable(
      name = "game_platform",
      joinColumns = @JoinColumn(name = "game_id"),
      inverseJoinColumns = @JoinColumn(name = "platform"))
  private Set<Platform> platforms = new HashSet<>();

  @Override
  public String toString() {
    return "Game{" + "name='" + name + '\'' + '}';
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
    Game game = (Game) o;
    return getId() != null && Objects.equals(getId(), game.getId());
  }

  @Override
  public final int hashCode() {
    return this instanceof HibernateProxy
        ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
        : getClass().hashCode();
  }
}
