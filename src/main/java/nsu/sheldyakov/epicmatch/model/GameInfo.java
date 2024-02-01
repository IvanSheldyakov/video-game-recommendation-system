package nsu.sheldyakov.epicmatch.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
public class GameInfo {

  private String id;
  private String name;
  private String score;
  private String releaseDate;
  private String summary;
  private String rating;
  private String publisher;
  private String platform;
  private String genre;
  private List<Double> vector;
}
