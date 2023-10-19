package com.example.demo.mapper;

import com.example.demo.domain.Game;
import com.example.demo.model.GameInfo;
import com.example.demo.model.SearchCriteria;
import java.util.List;

public class GameMapper {

  public static GameInfo toGameInfo(Game game, SearchCriteria criteria) {
    if (game == null) {
      return null;
    }

    String id = game.getId() != null ? game.getId().toString() : null;
    String name = game.getName();
    String score = game.getScore() != null ? game.getScore().toString() : null;
    String releaseDate = game.getReleaseDate() != null ? game.getReleaseDate().toString() : null;
    String summary = game.getSummary();

    String rating = game.getRating() != null ? game.getRating().getRating() : null;
    String publisher = criteria.getPublisher();

    String platform = criteria.getPlatform();
    String genre = criteria.getGenre();

    List<Double> vector = game.getVector();

    return new GameInfo(
        id, name, score, releaseDate, summary, rating, publisher, platform, genre, vector);
  }
}
