package nsu.sheldyakov.epicmatch.model;

import java.util.List;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameData {

  private String name;
  private Integer score;
  private String releaseDate;
  private String summary;
  private String rating;
  private String publisher;
  private List<String> platforms;
  private String genre;
  private String description;
}
