package nsu.sheldyakov.epicmatch.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import nsu.sheldyakov.epicmatch.domain.KeyWord;
import nsu.sheldyakov.epicmatch.domain.Type;
import nsu.sheldyakov.epicmatch.domain.WordCount;
import nsu.sheldyakov.epicmatch.model.Bounds;
import nsu.sheldyakov.epicmatch.repository.BlockWordRepository;
import nsu.sheldyakov.epicmatch.repository.KeyWordRepository;
import nsu.sheldyakov.epicmatch.repository.TypeRepository;
import nsu.sheldyakov.epicmatch.repository.WordCountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class KeyWordsService {

  private final WordCountRepository wordCountRepository;
  private final TypeRepository typeRepository;
  private final KeyWordRepository keyWordRepository;
  private final BlockWordRepository blockWordRepository;

  private boolean needBlock = false;

  private Map<Type, List<String>> typeAndKeyWordsMap;
  private Map<Integer, Type> vectorPositionAndTypeMap;

  @Transactional
  public void updateKeyWords() {
    //
    //    if (needBlock) {
    //      Set<BlockWord> wordCounts =
    //          wordCountRepository.findAll(Sort.by(Sort.Order.desc("count"))).stream()
    //              .limit(100)
    //              .map(
    //                  wordCount -> {
    //                    return new BlockWord(wordCount.getWord());
    //                  })
    //              .collect(Collectors.toSet());
    //      blockWordRepository.saveAll(wordCounts);
    //    }
    //    Set<String> blockWords =
    //
    // blockWordRepository.findAll().stream().map(BlockWord::getWord).collect(Collectors.toSet());
    //
    //    List<Type> types = typeRepository.findAll();
    //
    //    Set<String> allKeywords =
    //        types.stream()
    //            .flatMap(type ->
    // wordRepository.findWordsByType(type).stream().map(KeyWord::getWord))
    //            .collect(Collectors.toSet());
    //
    //    List<KeyWord> newKeyKeyWords = new ArrayList<>();
    //    types.forEach(
    //        type -> {
    //          List<WordCount> countList =
    //              wordCountRepository.findAllByTypeNameOrderByCountAsc(type.getTypeName());
    //          double gameCount = type.getGames().size() * 0.7;
    //
    //          if (countList.isEmpty()) return;
    //
    //          List<WordCount> list =
    //              countList.stream().filter(wordCount -> wordCount.getInGames() >
    // gameCount).toList();
    //
    //          Bounds bounds = findBounds(list);
    //
    //          list.stream()
    //              .filter(wordCount -> bounds.inBounds(wordCount.getCount()))
    //              .map(WordCount::getWord)
    //              .filter(word -> !allKeywords.contains(word))
    //              .filter(word -> !blockWords.contains(word))
    //              .forEach(
    //                  newKeyword -> {
    //                    newKeyKeyWords.add(new KeyWord(newKeyword, type));
    //                    allKeywords.add(newKeyword);
    //                  });
    //        });
    //    // wordRepository.saveAll(newKeyWords);
    //    System.out.println(newKeyKeyWords);
    //    System.out.println(newKeyKeyWords.size());
  }

  @PostConstruct
  public void init() { // TODO после обновления кл слов пересобрать
    updateTypeAndKeyWordsMap();
    collectVectorPositionAndTypeMap();
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

  private Bounds findBounds(List<WordCount> countList) {
    Double Q1 = findQ1(countList);
    Double Q3 = findQ3(countList);
    return new Bounds(Q1, Q3);
  }

  private Double findQ1(List<WordCount> countList) {
    double position = countList.size() * 0.25;
    return findQ(countList, position);
  }

  private Double findQ3(List<WordCount> countList) {
    double position = countList.size() * 0.75;
    return findQ(countList, position);
  }

  private Double findQ(List<WordCount> countList, double position) {
    if (position % 1 == 0) {
      // Если позиция является целым числом, возвращаем среднее значение двух элементов
      long left = countList.get((int) position - 1).getCount();
      long right = countList.get((int) position).getCount();
      return (left + right) / 2.0;
    } else {
      // Если позиция дробная, округляем ее вверх и возвращаем значение элемента на этой позиции
      return Long.valueOf(countList.get((int) Math.ceil(position) - 1).getCount()).doubleValue();
    }
  }
}
