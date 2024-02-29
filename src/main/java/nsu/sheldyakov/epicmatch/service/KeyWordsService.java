package nsu.sheldyakov.epicmatch.service;

import static nsu.sheldyakov.epicmatch.utils.Constants.STEP_TO_UPDATE_KEY_WORDS;
import static nsu.sheldyakov.epicmatch.utils.Constants.VECTOR_SIZE;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import nsu.sheldyakov.epicmatch.domain.KeyWord;
import nsu.sheldyakov.epicmatch.domain.KeyWordCandidate;
import nsu.sheldyakov.epicmatch.domain.Type;
import nsu.sheldyakov.epicmatch.domain.WordCount;
import nsu.sheldyakov.epicmatch.mapper.KeyWordMapper;
import nsu.sheldyakov.epicmatch.repository.*;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class KeyWordsService {

  private final WordCountRepository wordCountRepository;
  private final TypeRepository typeRepository;
  private final KeyWordRepository keyWordRepository;
  private final BlockWordRepository blockWordRepository;
  private final KeyWordCandidateRepository keyWordCandidateRepository;
  private final KeyWordMapper keyWordMapper;

  private boolean needBlock = false;

  private Map<Type, List<String>> typeAndKeyWordsMap;
  private Map<Integer, Type> vectorPositionAndTypeMap;

  public static final int WORD_LIMIT = 100;

  @Transactional
  public void updateKeyWords() {
    prepareKeyWordsCandidates();
    findAndSaveNewKeyWords();
    keyWordCandidateRepository.deleteAll();
  }

  private void findAndSaveNewKeyWords() {
    typeAndKeyWordsMap
        .keySet()
        .forEach(
            type -> {
              List<KeyWord> newKeyWords =
                  keyWordCandidateRepository.findByType(type).stream()
                      .sorted(
                          Comparator.comparing(KeyWordCandidate::getAverageTfidf)
                              .reversed()) // Сортировка по убыванию averageTfidf
                      .limit(5) // Ограничение списка первыми 5 элементами
                      .map(keyWordMapper::map)
                      .toList();
              keyWordRepository.saveAll(newKeyWords);
            });
  }

  private void prepareKeyWordsCandidates() {
    while (true) {
      List<String> words = wordCountRepository.findLimitedNumberOfWords(WORD_LIMIT);
      int amountOfWordsToAnalyze = words.size();

      if (amountOfWordsToAnalyze == 0) {
        break;
      }

      List<KeyWordCandidate> candidatesToSave = new ArrayList<>();
      words.forEach(
          word -> {
            List<WordCount> wordCounts = wordCountRepository.findByWord(word);
            RealVector sumVector = new ArrayRealVector(VECTOR_SIZE);
            int amountOfGames = wordCounts.size();
            double idf = calculateIdf(STEP_TO_UPDATE_KEY_WORDS, amountOfGames);
            double sumTfidf = 0;
            for (WordCount wordCount : wordCounts) {
              RealVector vector = new ArrayRealVector(wordCount.getGame().getVector());
              sumVector.add(vector.mapMultiply(wordCount.getRelativeFrequency()));
              sumTfidf = wordCount.getRelativeFrequency() * idf;
            }

            RealVector averageVector = sumVector.mapDivide(amountOfGames);
            Type typeOfWord = vectorPositionAndTypeMap.get(findIndexOfMaxElement(averageVector));
            double averageTfidf = sumTfidf / amountOfGames;
            KeyWordCandidate keyWordCandidate =
                new KeyWordCandidate(word, averageTfidf, typeOfWord);
            candidatesToSave.add(keyWordCandidate);
            wordCountRepository.deleteAll(wordCounts);
          });

      keyWordCandidateRepository.saveAll(candidatesToSave);
    }
  }

  private double calculateIdf(int totalNumDocs, int numDocsWithTerm) {
    return Math.log((double) totalNumDocs / numDocsWithTerm);
  }

  private int findIndexOfMaxElement(RealVector vector) {
    if (vector == null || vector.getDimension() == 0) {
      throw new IllegalArgumentException("Вектор пуст или null");
    }

    double max = vector.getEntry(0);
    int maxIndex = 0;

    for (int i = 1; i < vector.getDimension(); i++) {
      double current = vector.getEntry(i);
      if (current > max) {
        max = current;
        maxIndex = i;
      }
    }

    return maxIndex;
  }

  @PostConstruct
  public void init() { // TODO после обновления кл слов пересобрать
    updateTypeAndKeyWordsMap();
    collectVectorPositionAndTypeMap();
  }

  public int getAmountOfKeyWordsByVectorPosition(int position) {
    return typeAndKeyWordsMap.get(vectorPositionAndTypeMap.get(position)).size();
  }

  private void collectVectorPositionAndTypeMap() {
    List<Type> types = typeRepository.findAll();
    vectorPositionAndTypeMap =
        IntStream.range(0, types.size())
            .boxed()
            .collect(
                Collectors.toMap(
                    Function.identity(), // Ключ - индекс
                    types::get) // Значение - элемент списка
                );
  }

  public Map<Type, List<String>> getTypeAndKeyWordsMap() {
    return typeAndKeyWordsMap;
  }

  public Map<Integer, Type> getVectorPositionAndTypeMap() {
    return vectorPositionAndTypeMap;
  }

  private void updateTypeAndKeyWordsMap() {
    typeAndKeyWordsMap = collectTypeAndKeyWordsMap();
  }

  private Map<Type, List<String>> collectTypeAndKeyWordsMap() {
    return typeRepository.findAll().stream()
        .collect(
            Collectors.toMap(
                Function.identity(),
                getValueMapper(),
                (oldValue, newValue) -> oldValue, // в случае дубликатов, выбираем старое значение
                LinkedHashMap::new));
  }

  private Function<Type, List<String>> getValueMapper() {
    return type -> keyWordRepository.findWordsByType(type).stream().map(KeyWord::getWord).toList();
  }
}
