package nsu.sheldyakov.epicmatch.service;

import java.util.concurrent.atomic.AtomicBoolean;
import lombok.RequiredArgsConstructor;
import nsu.sheldyakov.epicmatch.repository.GameRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameAnalyzer {

  private static final long ONE_MINUTE = 60 * 1000;

  private final AtomicBoolean isTaskRunning = new AtomicBoolean(false);

  private final GameRepository gameRepository;

  @Scheduled(fixedRate = ONE_MINUTE)
  public void analyze() {
    if (isTaskRunning.compareAndSet(false, true)) {
      try {

      } finally {
        isTaskRunning.set(false);
      }
    }
  }
}
