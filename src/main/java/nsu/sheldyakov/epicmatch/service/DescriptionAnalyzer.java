package nsu.sheldyakov.epicmatch.service;

import static nsu.sheldyakov.epicmatch.utils.Constants.VECTOR_SIZE;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import nsu.sheldyakov.epicmatch.domain.Game;
import nsu.sheldyakov.epicmatch.domain.WordCount;
import nsu.sheldyakov.epicmatch.repository.GameRepository;
import nsu.sheldyakov.epicmatch.repository.WordCountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DescriptionAnalyzer {

  private final GameRepository gameRepository;
  private final KeyWordsService keywordsService;
  private final WordsFinder wordsFinder;
  private final WordCountRepository wordCountRepository;

  @Transactional
  public void calculateGameVectorAndSaveWordsStatistics(Game game) {
    List<String> wordsFromDescription =
        wordsFinder.find(game.getDescription()).stream()
            .filter(word -> !keywordsService.getBlockWords().contains(word))
            .toList();
    game.setVector(calculateGameVector(wordsFromDescription));
    Game savedGame = gameRepository.save(game);
    countWordsForFindingNewKeywords(savedGame, wordsFromDescription);
  }

  private Double[] calculateGameVector(List<String> words) {
    List<Integer> vector =
        keywordsService.getTypeAndKeyWordsMap().values().stream()
            .map(keyWords -> getCountOfMatches(words, keyWords))
            .toList();
    return getNormalizedVectorConsideringAmountOfKeyWordsForType(vector);
  }

  private Double[] getNormalizedVectorConsideringAmountOfKeyWordsForType(List<Integer> vector) {
    Double[] normalizedVectorConsideringAmountOfKeyWordsForType = new Double[VECTOR_SIZE];
    for (int i = 0; i < vector.size(); i++) {
      normalizedVectorConsideringAmountOfKeyWordsForType[i] =
          vector.get(i) / (double) keywordsService.getAmountOfKeyWordsByVectorPosition(i);
    }
    return normalizedVectorConsideringAmountOfKeyWordsForType;
  }

  private int getCountOfMatches(List<String> words, List<String> keyWords) {
    return (int) words.parallelStream().filter(keyWords::contains).count();
  }

  private void countWordsForFindingNewKeywords(Game game, List<String> words) {

    Long amountOfWordsInDescription = (long) words.size();

    Map<String, Long> wordCounts =
        words.parallelStream()
            .filter(word -> !keywordsService.getAllKeyWords().contains(word))
            .collect(Collectors.groupingBy(word -> word, Collectors.counting()));

    Set<WordCount> wordCountsToSave =
        wordCounts.entrySet().parallelStream()
            .map(entry -> map(game, amountOfWordsInDescription, entry))
            .collect(Collectors.toSet());

    wordCountRepository.saveAll(wordCountsToSave);
  }

  private WordCount map(Game game, Long amountOfWordsInDescription, Map.Entry<String, Long> entry) {
    return new WordCount(
        entry.getKey(), getRelativeFrequency(amountOfWordsInDescription, entry.getValue()), game);
  }

  private double getRelativeFrequency(Long amountOfWordsInDescription, Long value) {
    return value.doubleValue() / amountOfWordsInDescription.doubleValue();
  }
}
