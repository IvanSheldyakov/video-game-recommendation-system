package nsu.sheldyakov.epicmatch.service;

import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nsu.sheldyakov.epicmatch.domain.Game;
import nsu.sheldyakov.epicmatch.mapper.GameMapper;
import nsu.sheldyakov.epicmatch.repository.*;
import nsu.sheldyakov.epicmatch.utils.Constants;
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
  private final EntityManager em;

  public void collect(int firstPageNumber, int lastPageNumber) {
    for (int pageNumber = firstPageNumber; pageNumber <= lastPageNumber; pageNumber++) {
      try {
        List<String> gameUrls = bestGamesPageParser.getGamePageUrls(pageNumber);
        List<Game> gameList =
            gameUrls.stream()
                .map(gameUrl -> Constants.WWW_METACRITIC_COM + gameUrl)
                .map(gamePageParser::getGameData)
                .map(gameMapper::map)
                .toList();
        transactionWrapper.wrapInTransaction(
            () -> {
              for (Game game : gameList) {
                game.setRating(
                    em.merge(
                        game
                            .getRating())); // todo org.hibernate.id.IdentifierGenerationException:
                                            // ids for this class must be manually assigned before
                                            // calling save():
                                            // nsu.sheldyakov.epicmatch.domain.Rating
                game.setPublisher(em.merge(game.getPublisher()));
                game.setGenre(em.merge(game.getGenre()));
                game.setPlatforms(
                    game.getPlatforms().stream().map(em::merge).collect(Collectors.toSet()));
              }
              log.info(gameList.toString());
              gameRepository.saveAll(gameList);
            });

      } catch (Exception e) {
        log.error(
            "Collector failed for pageNumber={} with exception message={}",
            pageNumber,
            e.getMessage());
      }
    }
    log.info("done");
  }
}
