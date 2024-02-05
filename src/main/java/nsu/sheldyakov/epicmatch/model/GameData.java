package nsu.sheldyakov.epicmatch.model;

import java.time.LocalDate;
import java.util.Set;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameData {

  private String name;
  private Integer score;
  private LocalDate releaseDate;
  private String summary;
  private String rating;
  private String publisher;
  private Set<String> platforms;
  private String genre;
  private String description;
}
