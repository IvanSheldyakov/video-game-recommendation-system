package nsu.sheldyakov.epicmatch.service;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.RequiredArgsConstructor;
import nsu.sheldyakov.epicmatch.domain.Game;
import nsu.sheldyakov.epicmatch.repository.GameRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GameAnalyzer {

  private static final long HALF_MINUTE = 30 * 1000;

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
          descriptionAnalyzer.calculateGameVectorAndSaveWordsStatistics(maybeGame.get());
        } else {
          return;
        }

        long count = gameRepository.countAnalyzedGames();
        if (count % 100 == 0) {
          // update block words
          keyWordsService.updateKeyWords();
        }

      } finally {
        isTaskRunning.set(false);
      }
    }
  }
}
