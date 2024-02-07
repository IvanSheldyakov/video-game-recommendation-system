package nsu.sheldyakov.epicmatch.service;

import static nsu.sheldyakov.epicmatch.utils.Constants.WWW_METACRITIC_COM;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nsu.sheldyakov.epicmatch.domain.*;
import nsu.sheldyakov.epicmatch.mapper.GameMapper;
import nsu.sheldyakov.epicmatch.repository.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MetacriticGameInfoCollector {

  private final BestGamesPageParser bestGamesPageParser;
  private final GamePageParser gamePageParser;
  private final GameRepository gameRepository;
  private final GenreRepository genreRepository;
  private final PlatformRepository platformRepository;
  private final PublisherRepository publisherRepository;
  private final RatingRepository ratingRepository;
  private final GameMapper gameMapper;
  private final TransactionWrapper transactionWrapper;

  private final Map<String, Rating> ratingCache = new HashMap<>();
  private final Map<String, Publisher> publisherCache = new HashMap<>();
  private final Map<String, Genre> genreCache = new HashMap<>();
  private final Map<String, Platform> platformCache = new HashMap<>();

  public void collect(int firstPageNumber, int lastPageNumber) {
    for (int pageNumber = firstPageNumber; pageNumber <= lastPageNumber; pageNumber++) {
      try {
        List<String> gameUrls = bestGamesPageParser.getGamePageUrls(pageNumber);
        for (String gameUrl : gameUrls) {
          Game game = gameMapper.map(gamePageParser.getGameData(WWW_METACRITIC_COM + gameUrl));

          transactionWrapper.wrapInTransaction(
              () -> {
                saveGameConnectedEntities(game);
                log.info(game.toString());
                gameRepository.save(game);
              });
        }

      } catch (Exception e) {
        log.error(
            "Collector failed for pageNumber={} with exception message={}",
            pageNumber,
            e.getMessage());
      }
    }
    log.info("done");
  }

  private void saveGameConnectedEntities(Game game) {
    Rating rating = game.getRating();
    Publisher publisher = game.getPublisher();
    Genre genre = game.getGenre();

    if (ratingCache.containsKey(rating.getName())) {
      game.setRating(ratingCache.get(rating.getName()));
    } else {
      Rating newRating = ratingRepository.save(rating);
      ratingCache.put(newRating.getName(), newRating);
      game.setRating(newRating);
    }

    if (publisherCache.containsKey(publisher.getName())) {
      game.setPublisher(publisherCache.get(publisher.getName()));
    } else {
      Publisher newPublisher = publisherRepository.save(publisher);
      publisherCache.put(newPublisher.getName(), newPublisher);
      game.setPublisher(newPublisher);
    }

    if (genreCache.containsKey(genre.getName())) {
      game.setGenre(genreCache.get(genre.getName()));
    } else {
      Genre newGenre = genreRepository.save(genre);
      genreCache.put(newGenre.getName(), newGenre);
      game.setGenre(newGenre);
    }

    Set<Platform> processedPlatforms =
        game.getPlatforms().stream()
            .map(
                platform -> {
                  if (platformCache.containsKey(platform.getName())) {
                    return platformCache.get(platform.getName());
                  } else {
                    Platform newPlatform = platformRepository.save(platform);
                    platformCache.put(newPlatform.getName(), newPlatform);
                    return newPlatform;
                  }
                })
            .collect(Collectors.toSet());
    game.setPlatforms(processedPlatforms);
  }
}
