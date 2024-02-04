package nsu.sheldyakov.epicmatch.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nsu.sheldyakov.epicmatch.model.GameData;
import nsu.sheldyakov.epicmatch.utils.Constants;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MetacriticGameInfoCollector {

  private final BestGamesPageParser bestGamesPageParser;
  private final GamePageParser gamePageParser;

  public void collect(int firstPageNumber, int lastPageNumber) {
    try {
      for (int pageNumber = firstPageNumber; pageNumber <= lastPageNumber; pageNumber++) {
        List<String> gameUrls = bestGamesPageParser.getGamePageUrls(pageNumber);
        List<GameData> gameDataList =
            gameUrls.stream()
                .map(gameUrl -> Constants.WWW_METACRITIC_COM + gameUrl)
                .map(gamePageParser::getGameData)
                .toList();
      }
      log.info("done");

    } catch (Exception e) {
      log.error(e.getMessage());
    }
  }
}
