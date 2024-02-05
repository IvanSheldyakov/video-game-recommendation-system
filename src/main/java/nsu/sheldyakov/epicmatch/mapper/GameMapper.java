package nsu.sheldyakov.epicmatch.mapper;

import java.util.Set;
import java.util.stream.Collectors;
import nsu.sheldyakov.epicmatch.domain.Game;
import nsu.sheldyakov.epicmatch.domain.Platform;
import nsu.sheldyakov.epicmatch.model.GameData;
import nsu.sheldyakov.epicmatch.model.GameInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper
public interface GameMapper {

  //  public static GameInfo toGameInfo(Game game) {
  //    if (game == null) {
  //      return null;
  //    }
  //
  //    String id = game.getId() != null ? game.getId().toString() : null;
  //    String name = game.getName();
  //    String score = game.getScore() != null ? game.getScore().toString() : null;
  //    String releaseDate = game.getReleaseDate() != null ? game.getReleaseDate().toString() :
  // null;
  //    String summary =
  //        TextFormatter.removeTextInBracketsAfterDot(game.getSummary()); // TODO перенести
  //
  //    String rating = game.getRating() != null ? game.getRating().getName() : null;
  //    String publisher = game.getPublisher() != null ? game.getPublisher().getName() : null;
  //
  //    String platform =
  //        game.getPlatforms().stream()
  //            .map(Platform::getPlatform)
  //            .collect(Collectors.joining()); // TODO set?
  //    String genres =
  //        game.getGenres().stream().map(Genre::getGenre).collect(Collectors.joining(", "));
  //
  //    return new GameInfo(
  //        id, name, score, releaseDate, summary, rating, publisher, platform, genres, List.of());
  //  }

  @Mapping(target = "rating", source = "rating.name")
  @Mapping(target = "genre", source = "genre.name")
  @Mapping(target = "publisher", source = "publisher.name")
  @Mapping(target = "platform", source = "platforms", qualifiedByName = "mapPlatformsToString")
  GameInfo map(Game game);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "vector", ignore = true)
  @Mapping(target = "rating.name", source = "rating")
  @Mapping(target = "publisher.name", source = "publisher")
  @Mapping(target = "genre.name", source = "genre")
  @Mapping(target = "platforms", source = "platforms", qualifiedByName = "mapPlatforms")
  Game map(GameData gameData);

  @Named("mapPlatforms")
  default Set<Platform> mapPlatforms(Set<String> platforms) {
    return platforms.stream().map(Platform::new).collect(Collectors.toSet());
  }

  @Named("mapPlatformsToString")
  default String mapPlatformsToString(Set<Platform> platforms) {
    return platforms.stream().map(Platform::getName).collect(Collectors.joining(" "));
  }
}
