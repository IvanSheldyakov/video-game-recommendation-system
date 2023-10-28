package com.example.demo.mapper;

import com.example.demo.domain.Game;
import com.example.demo.domain.Genre;
import com.example.demo.domain.Platform;
import com.example.demo.model.GameInfo;
import com.example.demo.utils.TextFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class GameMapper {

  public static GameInfo toGameInfo(Game game) {
    if (game == null) {
      return null;
    }

    String id = game.getId() != null ? game.getId().toString() : null;
    String name = game.getName();
    String score = game.getScore() != null ? game.getScore().toString() : null;
    String releaseDate = game.getReleaseDate() != null ? game.getReleaseDate().toString() : null;
    String summary =
        TextFormatter.removeTextInBracketsAfterDot(game.getSummary()); // TODO перенести

    String rating = game.getRating() != null ? game.getRating().getRating() : null;
    String publisher = game.getPublisher() != null ? game.getPublisher().getPublisher() : null;

    String platform =
        game.getPlatforms().stream()
            .map(Platform::getPlatform)
            .collect(Collectors.joining()); // TODO set?
    String genres =
        game.getGenres().stream().map(Genre::getGenre).collect(Collectors.joining(", "));

    return new GameInfo(
        id, name, score, releaseDate, summary, rating, publisher, platform, genres, List.of());
  }
}
