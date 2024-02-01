package nsu.sheldyakov.epicmatch.domain;

import java.util.List;
import javax.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "rating")
@Getter
@Setter
@NoArgsConstructor
public class Rating {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String rating;

  @OneToMany(mappedBy = "rating")
  private List<Game> games;

  public Rating(String rating) {
    this.rating = rating;
  }
}
