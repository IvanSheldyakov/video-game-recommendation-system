// package nsu.sheldyakov.epicmatch.service;
//
// import java.io.IOException;
// import java.time.LocalDate;
// import java.util.*;
// import java.util.concurrent.CompletableFuture;
// import java.util.stream.Collectors;
// import lombok.RequiredArgsConstructor;
// import nsu.sheldyakov.epicmatch.domain.*;
// import nsu.sheldyakov.epicmatch.repository.*;
// import nsu.sheldyakov.epicmatch.utils.Constants;
// import nsu.sheldyakov.epicmatch.utils.MonthConverter;
// import nsu.sheldyakov.epicmatch.utils.VectorNormalizer;
// import org.jsoup.Jsoup;
// import org.jsoup.nodes.Document;
// import org.jsoup.nodes.Element;
// import org.openqa.selenium.WebDriver;
// import org.springframework.transaction.annotation.Transactional;
//
// @RequiredArgsConstructor
// public class GameHandler extends Thread {
//
//  private final String gameUrl;
//  private final WebDriver webDriver;
//  private final GameRepository gameRepository;
//  private final TypeRepository typeRepository;
//  private final WordRepository wordRepository;
//  private final WordCountRepository wordCountRepository;
//  private final GenreRepository genreRepository;
//  private final PlatformRepository platformRepository;
//  private final PublisherRepository publisherRepository;
//  private final RatingRepository ratingRepository;
//  private final WordsFinder wordsFinder = new WordsFinder();
//
//  public static boolean findNewKeywords = true;
//
//  @Override
//  @Transactional
//  public void run() {
//    try {
//      StringBuilder gameDescription = new StringBuilder();
//      //fillGameTextDescription(gameDescription);
//      var map = wordsFinder.find(gameDescription.toString());
//
//    //  Game game = gameRepository.save(fillGameInfo());
//
//      analyzeTypeOfGame(map, game);
//      System.out.println(game);
//
//    } catch (IOException e) {
//      System.out.println(e.getMessage());
//    }
//  }
//
//
//  private void analyzeTypeOfGame(HashMap<String, List<String>> map, Game game) {
//    List<String> allWords = map.values().stream().flatMap(List::stream).toList();
//
//    List<Type> types = typeRepository.findAll();
//    List<Integer> vector =
//        types.stream()
//            .map(
//                type -> {
//                  List<String> wordList =
//                      wordRepository.findWordsByType(type).stream()
//                          .parallel()
//                          .map(KeyWord::getWord)
//                          .toList();
//                  return (int) allWords.stream().parallel().filter(wordList::contains).count();
//                })
//            .toList();
//
//    List<CompletableFuture<?>> l = List.of();
//    game.setVector(VectorNormalizer.normalize(vector));
//    Type type = getType(vector);
//    game.setType(type);
//    if (findNewKeywords) {
//      countWordsForFindingNewKeywords(type.getTypeName(), allWords);
//    }
//  }
//
//  private Type getType(List<Integer> vector) {
//    Integer idOfType = vector.indexOf(Collections.max(vector)) + 1;
//    return typeRepository.findById(idOfType).orElseThrow();
//  }
//
//  private void countWordsForFindingNewKeywords(String typeName, List<String> allWords) {
//
//    Set<WordCount> wordCountsToSave = new HashSet<>();
//    Map<String, Long> wordCounts =
//        allWords.stream()
//            .parallel()
//            .collect(Collectors.groupingBy(word -> word, Collectors.counting()));
//
//    wordCounts.forEach(
//        (word, count) -> {
//          Optional<WordCount> maybeWordCount =
//              wordCountRepository.findByWordAndTypeName(word, typeName);
//          if (maybeWordCount.isPresent()) {
//            WordCount wordCount = maybeWordCount.get();
//            wordCount.setCount(wordCount.getCount() + count);
//            wordCount.setInGames(wordCount.getInGames() + 1);
//            wordCountsToSave.add(wordCount);
//          } else {
//            WordCount newWordCount = new WordCount();
//            newWordCount.setCount(count);
//            newWordCount.setTypeName(typeName);
//            newWordCount.setWord(word);
//            newWordCount.setInGames(1);
//            wordCountsToSave.add(newWordCount);
//          }
//        });
//    wordCountRepository.saveAll(wordCountsToSave);
//  }
// }
