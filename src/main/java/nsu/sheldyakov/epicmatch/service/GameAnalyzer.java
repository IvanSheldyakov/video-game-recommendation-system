package nsu.sheldyakov.epicmatch.service;

import static nsu.sheldyakov.epicmatch.utils.Constants.STEP_TO_UPDATE_KEY_WORDS;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nsu.sheldyakov.epicmatch.domain.Game;
import nsu.sheldyakov.epicmatch.repository.GameRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameAnalyzer {

  private static final long HALF_MINUTE = 3 * 1000;

  private final AtomicBoolean isTaskRunning = new AtomicBoolean(false);

  private final GameRepository gameRepository;
  private final DescriptionAnalyzer descriptionAnalyzer;
  private final KeyWordsService keyWordsService;

  @Scheduled(fixedRate = HALF_MINUTE)
  @Transactional
  public void analyze() {

    if (isTaskRunning.compareAndSet(false, true)) {
      try {
        Optional<Game> maybeGame = gameRepository.findNotAnalyzedGame();
        if (maybeGame.isPresent()) {
          Game game = maybeGame.get();
          log.info("Start analyze game - {}", game.getName());
          descriptionAnalyzer.calculateGameVectorAndSaveWordsStatistics(game);
          log.info("Finish analyze game - {}", game.getName());
        } else {
          return;
        }

        long countAnalyzedGames = gameRepository.countAnalyzedGames();
        log.info("Amount of analyzed games - {}", countAnalyzedGames);
        if (countAnalyzedGames % STEP_TO_UPDATE_KEY_WORDS == 0) {
          log.info("Start update key words");
          keyWordsService.updateKeyWords();
          log.info("End update key words");
        }

      } finally {
        isTaskRunning.set(false);
      }
    }
  }
}
