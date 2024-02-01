package nsu.sheldyakov.epicmatch.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GameTypeBlock {

  private String id;
  private String title;
  private String description;
}
