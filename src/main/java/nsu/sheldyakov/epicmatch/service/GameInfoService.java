package nsu.sheldyakov.epicmatch.service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import nsu.sheldyakov.epicmatch.domain.*;
import nsu.sheldyakov.epicmatch.mapper.GameMapper;
import nsu.sheldyakov.epicmatch.model.GameInfo;
import nsu.sheldyakov.epicmatch.model.GameTypeBlock;
import nsu.sheldyakov.epicmatch.model.Page;
import nsu.sheldyakov.epicmatch.model.SearchCriteria;
import nsu.sheldyakov.epicmatch.repository.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameInfoService {

  private static final int LIMIT = 10;
  private final TypeRepository typeRepository;
  private final PublisherRepository publisherRepository;
  private final GenreRepository genreRepository;
  private final PlatformRepository platformRepository;
  private final RatingRepository ratingRepository;
  private final GameRepository gameRepository;
  private final GameMapper gameMapper;

  public List<GameTypeBlock> getGameTypes() {
    return typeRepository.findAll().stream().map(this::map).toList();
  }

  public List<String> getPublishers() {
    return publisherRepository.findAll().stream().map(Publisher::getName).toList();
  }

  public List<String> getGenres() {
    return genreRepository.findAll().stream().map(Genre::getName).toList();
  }

  public List<String> getPlatforms() {
    return platformRepository.findAll().stream().map(Platform::getName).toList();
  }

  public List<String> getRatings() {
    return ratingRepository.findAll().stream().map(Rating::getName).toList();
  }

  public Page getPageOfGameInfos(SearchCriteria searchCriteria, int page) {
    String vector = convertListToString(searchCriteria.getCustomValues());

    long count =
        gameRepository.countGameInfoByFilter(
            searchCriteria.getPlatform(),
            searchCriteria.getMinScore(),
            searchCriteria.getGenre(),
            searchCriteria.getPublisher(),
            searchCriteria.getRating(),
            searchCriteria.getReleaseStartDate(),
            searchCriteria.getReleaseStartDate());

    int totalPages = (int) Math.ceil((double) count / LIMIT);

    int offset = page * LIMIT;

    List<Game> games =
        gameRepository.findGameInfoByFilter(
            searchCriteria.getPlatform(),
            searchCriteria.getMinScore(),
            searchCriteria.getGenre(),
            searchCriteria.getPublisher(),
            searchCriteria.getRating(),
            searchCriteria.getReleaseStartDate(),
            searchCriteria.getReleaseStartDate(),
            vector,
            LIMIT,
            offset);

    List<GameInfo> gameInfos =
        games.stream()
            .map(gameMapper::map)
            .collect(Collectors.toCollection(LinkedHashSet::new))
            .stream()
            .collect(Collectors.toList());

    return new Page(totalPages, gameInfos);
  }

  private GameTypeBlock map(Type type) {
    return new GameTypeBlock(type.getName(), type.getDescription());
  }

  private String convertListToString(List<String> list) {
    return list.stream()
        .map(Integer::parseInt)
        .map(String::valueOf)
        .collect(Collectors.joining(","));
  }
}
