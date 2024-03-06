package nsu.sheldyakov.epicmatch.model;

import java.util.Objects;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof GameInfo gameInfo)) return false;
    return Objects.equals(getId(), gameInfo.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }
}
