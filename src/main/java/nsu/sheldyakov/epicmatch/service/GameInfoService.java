package nsu.sheldyakov.epicmatch.service;

import nsu.sheldyakov.epicmatch.domain.*;
import nsu.sheldyakov.epicmatch.mapper.GameMapper;
import nsu.sheldyakov.epicmatch.model.GameInfo;
import nsu.sheldyakov.epicmatch.model.GameTypeBlock;
import nsu.sheldyakov.epicmatch.model.Page;
import nsu.sheldyakov.epicmatch.model.SearchCriteria;
import nsu.sheldyakov.epicmatch.repository.*;
import nsu.sheldyakov.epicmatch.utils.VectorNormalizer;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
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

  public List<GameTypeBlock> getGameTypes() {
    return typeRepository.findAll().stream().map(this::map).toList();
  }

  public List<String> getPublishers() {
    return publisherRepository.findAll(Sort.by(Sort.Order.asc("publisher"))).stream()
        .map(Publisher::getPublisher)
        .toList();
  }

  public List<String> getGenres() {
    return genreRepository.findAll(Sort.by(Sort.Order.asc("genre"))).stream()
        .map(Genre::getGenre)
        .toList();
  }

  public List<String> getPlatforms() {
    return platformRepository.findAll(Sort.by(Sort.Order.asc("platform"))).stream()
        .map(Platform::getPlatform)
        .toList();
  }

  public List<String> getRatings() {
    return ratingRepository.findAll(Sort.by(Sort.Order.asc("rating"))).stream()
        .map(Rating::getRating)
        .toList();
  }

  public Page getPageOfGameInfos(SearchCriteria searchCriteria, int page) {
    String vector = convertListToStringAndNormalize(searchCriteria.getCustomValues());

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

    List<GameInfo> gameInfos = games.stream().map(GameMapper::toGameInfo).toList();

    return new Page(totalPages, gameInfos);
  }

  private GameTypeBlock map(Type type) {
    return new GameTypeBlock(type.getId().toString(), type.getTypeName(), type.getDescription());
  }

  private String convertListToStringAndNormalize(List<String> list) {
    List<Integer> integerList = list.stream().map(Integer::parseInt).toList();
    Double[] vector = VectorNormalizer.normalize(integerList);
    return Arrays.stream(vector).map(String::valueOf).collect(Collectors.joining(","));
  }
}
