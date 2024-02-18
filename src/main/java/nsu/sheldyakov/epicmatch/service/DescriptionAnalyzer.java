package nsu.sheldyakov.epicmatch.service;

import static nsu.sheldyakov.epicmatch.utils.VectorNormalizer.normalize;

import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import nsu.sheldyakov.epicmatch.domain.Game;
import nsu.sheldyakov.epicmatch.domain.Type;
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

    List<String> wordsFromDescription = wordsFinder.find(game.getDescription());
    Double[] vector = calculateGameVector(wordsFromDescription);
    game.setVector(vector);
    gameRepository.save(game);
    countWordsForFindingNewKeywords(getTypeByVector(vector), wordsFromDescription);
  }

  private Type getTypeByVector(Double[] vector) {
    var list = Arrays.stream(vector).toList();
    Integer idOfType = list.indexOf(Collections.max(list));
    return keywordsService.getVectorPositionAndTypeMap().get(idOfType);
  }

  private Double[] calculateGameVector(List<String> words) {
    List<Integer> vector =
        keywordsService.getTypeAndKeyWordsMap().values().stream()
            .map(keyWords -> getCountOfMatches(words, keyWords))
            .toList();
    return normalize(vector);
  }

  private int getCountOfMatches(List<String> words, List<String> keyWords) {
    return (int) words.parallelStream().filter(keyWords::contains).count();
  }

  private void countWordsForFindingNewKeywords(Type type, List<String> words) {

    Set<WordCount> wordCountsToSave = new HashSet<>();
    Map<String, Long> wordCounts =
        words.stream()
            .parallel()
            .collect(Collectors.groupingBy(word -> word, Collectors.counting()));

    wordCounts.forEach(
        (word, count) -> {
          Optional<WordCount> maybeWordCount = wordCountRepository.findByWordAndType(word, type);
          if (maybeWordCount.isPresent()) {
            WordCount wordCount = maybeWordCount.get();
            wordCount.setCount(wordCount.getCount() + count);
            wordCountsToSave.add(wordCount);
          } else {
            WordCount newWordCount = new WordCount();
            newWordCount.setCount(count);
            newWordCount.setType(type);
            newWordCount.setWord(word);
            wordCountsToSave.add(newWordCount);
          }
        });
    wordCountRepository.saveAll(wordCountsToSave);
  }
}
