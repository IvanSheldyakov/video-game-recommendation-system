package nsu.sheldyakov.epicmatch.domain;

import java.util.List;
import java.util.Objects;
import javax.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.proxy.HibernateProxy;

@Entity
@Table(name = "type")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Type {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String typeName;

  @Column(name = "description", columnDefinition = "text")
  private String description;

  @OneToMany(mappedBy = "type")
  @ToString.Exclude
  private List<Word> words;

  @OneToMany(mappedBy = "type")
  @ToString.Exclude
  private List<Game> games;

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
    Type type = (Type) o;
    return getId() != null && Objects.equals(getId(), type.getId());
  }

  @Override
  public final int hashCode() {
    return this instanceof HibernateProxy
        ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
        : getClass().hashCode();
  }
}
