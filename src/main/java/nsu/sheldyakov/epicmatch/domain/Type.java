package nsu.sheldyakov.epicmatch.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

@Entity
@Table(name = "game_type")
@Getter
@Setter
@NoArgsConstructor
public class Type {

  @Id private String name;

  @Column(name = "description", columnDefinition = "text")
  private String description;

  @OneToMany(mappedBy = "type", cascade = CascadeType.ALL)
  private List<KeyWord> keyWords = new ArrayList<>();

  @OneToMany(mappedBy = "type", cascade = CascadeType.ALL)
  private List<KeyWord> wordCounts = new ArrayList<>();

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
    return getName() != null && Objects.equals(getName(), type.getName());
  }

  @Override
  public final int hashCode() {
    return this instanceof HibernateProxy
        ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
        : getClass().hashCode();
  }
}
