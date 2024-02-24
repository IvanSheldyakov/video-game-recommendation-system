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
  public void updateKeyWords() {}

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
}
