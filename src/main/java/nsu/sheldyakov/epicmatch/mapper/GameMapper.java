package nsu.sheldyakov.epicmatch.mapper;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import nsu.sheldyakov.epicmatch.domain.*;
import nsu.sheldyakov.epicmatch.model.GameData;
import nsu.sheldyakov.epicmatch.model.GameInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper
public interface GameMapper {

  @Mapping(target = "rating", source = "rating.name")
  @Mapping(target = "genre", source = "genre.name")
  @Mapping(target = "publisher", source = "publisher.name")
  @Mapping(target = "platform", source = "platforms", qualifiedByName = "mapPlatformsToString")
  GameInfo map(Game game);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "vector", ignore = true)
  @Mapping(target = "rating", source = "rating", qualifiedByName = "mapRating")
  @Mapping(target = "publisher", source = "publisher", qualifiedByName = "mapPublisher")
  @Mapping(target = "genre", source = "genre", qualifiedByName = "mapGenre")
  @Mapping(target = "platforms", source = "platforms", qualifiedByName = "mapPlatforms")
  Game map(GameData gameData);

  @Named("mapGenre")
  default Genre mapGenre(String genre) {
    return Objects.isNull(genre) ? new Genre("Нет") : new Genre(genre);
  }

  @Named("mapRating")
  default Rating mapRating(String rating) {
    return Objects.isNull(rating) ? new Rating("Нет") : new Rating(rating);
  }

  @Named("mapPublisher")
  default Publisher mapPublisher(String publisher) {
    return Objects.isNull(publisher) ? new Publisher("Нет") : new Publisher(publisher);
  }

  @Named("mapPlatforms")
  default Set<Platform> mapPlatforms(Set<String> platforms) {
    return platforms.stream().map(Platform::new).collect(Collectors.toSet());
  }

  @Named("mapPlatformsToString")
  default String mapPlatformsToString(Set<Platform> platforms) {
    return platforms.stream().map(Platform::getName).collect(Collectors.joining(" "));
  }
}
