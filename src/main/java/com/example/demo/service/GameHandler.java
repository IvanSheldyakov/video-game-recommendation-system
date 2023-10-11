package com.example.demo.service;

import com.example.demo.domain.*;
import com.example.demo.repository.*;
import com.example.demo.utils.Constants;
import com.example.demo.utils.MonthConverter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.WebDriver;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class GameHandler extends Thread {

  private final String gameUrl;
  private final WebDriver webDriver;
  private final GameRepository gameRepository;
  private final TypeRepository typeRepository;
  private final WordRepository wordRepository;
  private final WordCountRepository wordCountRepository;
  private final GenreRepository genreRepository;
  private final PlatformRepository platformRepository;
  private final PublisherRepository publisherRepository;
  private final RatingRepository ratingRepository;
  private final WordsFinder wordsFinder = new WordsFinder();

  public static boolean findNewKeywords = true;

  @Override
  @Transactional
  public void run() {
    try {
      StringBuilder gameDescription = new StringBuilder();
      fillGameTextDescription(gameDescription);
      var map = wordsFinder.find(gameDescription.toString());

      Game game = gameRepository.save(fillGameInfo());

      analyzeTypeOfGame(map, game);
      System.out.println(game);

    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
  }

  private void fillGameTextDescription(StringBuilder text) throws IOException {
    Document criticReviewsDoc = getCriticReviewsDoc();
    var elements =
        criticReviewsDoc
            .body()
            .select("div.c-pageProductReviews_row")
            .select("div.c-siteReview_quote");

    for (var elem : elements) {
      text.append(elem.text()).append(" ");
    }

    getIgnReview(criticReviewsDoc, text);
  }

  private Document getCriticReviewsDoc() {
    webDriver.get(gameUrl + Constants.TO_CRITIC_REVIEWS);
    String pageSource = webDriver.getPageSource();
    return Jsoup.parse(pageSource);
  }

  private Game fillGameInfo() throws IOException {
    webDriver.get(gameUrl);
    Document gameDoc = Jsoup.parse(webDriver.getPageSource());

    String gameName = getGameName(gameDoc);
    System.out.println(gameName);
    Integer score = getScore(gameDoc);
    System.out.println(score);
    String date = getDate(gameDoc);
    System.out.println(date);

    Rating rating = getRating(gameDoc);
    System.out.println(rating);
    Set<Genre> genres = getGenres(gameDoc);
    System.out.println(genres);
    Set<Platform> platforms = getPlatform(gameDoc);
    System.out.println(platforms);
    Publisher publisher = getPublisher(gameDoc);
    System.out.println(publisher);

    Document gameDetails = Jsoup.connect(gameUrl + Constants.TO_DETAILS).get();
    String summary = getSummary(gameDetails);
    System.out.println(summary);

    Game game = new Game();
    game.setName(gameName);
    game.getPlatforms().addAll(platforms);
    game.getGenres().addAll(genres);
    game.setScore(score);
    game.setReleaseDate(getReleaseDate(date));
    game.setPublisher(publisher);
    game.setRating(rating);
    game.setSummary(summary);
    return game;
  }

  private String getDate(Document gameDoc) {
    return gameDoc
        .body()
        .select("#__layout > div > div.c-layoutDefault_page > div.c-pageProductGame")
        .select(
            "div.c-productHero_player-scoreInfo.u-grid.g-grid-container > div.c-productHero_score-container.u-flexbox.u-flexbox-column.g-bg-white > div.g-text-xsmall > span.u-text-uppercase")
        .get(0)
        .text()
        .trim();
  }

  private Integer getScore(Document gameDoc) {
    return Integer.parseInt(
        gameDoc
            .body()
            .select("#__layout > div > div.c-layoutDefault_page > div.c-pageProductGame")
            .select(
                "div.c-productHero_player-scoreInfo.u-grid.g-grid-container > div.c-productHero_score-container.u-flexbox.u-flexbox-column.g-bg-white > div.c-productHero_scoreInfo.g-inner-spacing-top-medium.g-outer-spacing-bottom-medium.g-outer-spacing-top-medium > div:nth-child(1) > div > div.c-productScoreInfo_scoreContent.u-flexbox.u-flexbox-alignCenter.u-flexbox-justifyFlexEnd.g-width-100.u-flexbox-nowrap > div.c-productScoreInfo_scoreNumber.u-float-right > div > div > span")
            .get(0)
            .text()
            .trim());
  }

  private String getGameName(Document gameDoc) {
    return gameDoc
        .body()
        .select("#__layout > div > div.c-layoutDefault_page > div.c-pageProductGame")
        .select(
            "div.c-productHero_player-scoreInfo.u-grid.g-grid-container > div.c-productHero_score-container.u-flexbox.u-flexbox-column.g-bg-white > div.c-productHero_title.g-inner-spacing-bottom-medium.g-outer-spacing-top-medium > div")
        .get(0)
        .text()
        .trim();
  }

  private Set<Platform> getPlatform(Document doc) {
    var platforms =
        doc.body()
            .select(
                "#__layout > div > div.c-layoutDefault_page > div.c-pageProductGame > div.c-pageProduct_row.g-grid-container.c-pageProductionDetails > div > div > div.c-productionDetailsGame_grid.u-grid > div.c-gameDetails")
            .select("div.c-gameDetails_Platforms.u-flexbox.u-flexbox-row")
            .select("li.c-gameDetails_listItem");
    return platforms.stream()
        .map(Element::text)
        .map(platform -> platformRepository.findByPlatform(platform).orElse(new Platform(platform)))
        .collect(Collectors.toSet());
  }

  private String getTrailerUrl(Document doc) {
    Element element = doc.getElementById("videoContainer_wrapper");
    if (element == null) {
      return null;
    } else {
      return element.attr("data-mcvideourl").trim();
    }
  }

  private Publisher getPublisher(Document doc) {
    String publisher =
        doc.body()
            .select(
                "#__layout > div > div.c-layoutDefault_page > div.c-pageProductGame > div.c-pageProduct_row.g-grid-container.c-pageProductionDetails > div > div > div.c-productionDetailsGame_grid.u-grid > div.c-gameDetails")
            .select(
                "div.c-gameDetails_Distributor.u-flexbox.u-flexbox-row > span.g-outer-spacing-left-medium-fluid.g-color-gray70.u-block")
            .text();
    return publisherRepository.findByPublisher(publisher).orElse(new Publisher(publisher));
  }

  private Rating getRating(Document doc) {
    var el =
        doc.body()
            .select(
                "#__layout > div > div.c-layoutDefault_page > div.c-pageProductGame > div.c-pageProduct_row.g-grid-container.c-pageProductionDetails > div > div > div.c-productionDetailsGame_grid.u-grid > div.c-productionDetailsGame-summary.g-outer-spacing-bottom-small.g-container-rounded-small > div > div > div.c-productionDetailsGame_esrb_title.u-inline-block.g-outer-spacing-left-medium-fluid");

    if (el.size() < 1) {
      return null;
    } else {
      String rating = el.text().trim();
      return ratingRepository.findByRating(rating).orElse(new Rating(rating));
    }
  }

  private Set<Genre> getGenres(Document doc) {
    String genre =
        doc.body()
            .select(
                "#__layout > div > div.c-layoutDefault_page > div.c-pageProductGame > div.c-pageProduct_row.g-grid-container.c-pageProductionDetails > div > div > div.c-productionDetailsGame_grid.u-grid > div.c-gameDetails > div.c-gameDetails_sectionContainer.u-flexbox.u-flexbox-row.u-flexbox-alignBaseline > ul > li > div > a > span")
            .text()
            .trim();
    return Set.of(genreRepository.findByGenre(genre).orElse(new Genre(genre)));
  }

  private String getSummary(Document doc) { // TODO
    String description =
        doc.body()
            .select(
                "#__layout > div > div.c-layoutDefault_page > div.c-pageProductGame > div.c-pageProduct_row.g-grid-container.c-pageProductionDetails > div > div > div.c-productionDetailsGame_grid.u-grid > div.c-productionDetailsGame-summary.g-outer-spacing-bottom-small.g-container-rounded-small > p > span.c-productionDetailsGame_description.g-text-xsmall")
            .text()
            .trim();
    if (description.isEmpty()) {
      return null;
    }
    return description.substring("Description: ".length(), description.length());
  }

  private LocalDate getReleaseDate(String date) {
    String[] parts = date.split(",");
    String[] monthAndDay = parts[0].split(" ");
    int year = Integer.parseInt(parts[1].trim());
    int month = MonthConverter.convert(monthAndDay[0]);
    int day = Integer.parseInt(monthAndDay[1]);
    return LocalDate.of(year, month, day);
  }

  private void analyzeTypeOfGame(HashMap<String, List<String>> map, Game game) {
    List<String> allWords = map.values().stream().flatMap(List::stream).toList();

    List<Type> types = typeRepository.findAll();
    List<Integer> vector =
        types.stream()
            .map(
                type -> {
                  List<String> wordList =
                      wordRepository.findWordsByType(type).stream()
                          .parallel()
                          .map(Word::getWord)
                          .toList();
                  return (int) allWords.stream().parallel().filter(wordList::contains).count();
                })
            .toList();

    game.setVector(vector);
    Type type = getType(vector);
    game.setType(type);
    if (findNewKeywords) {
      countWordsForFindingNewKeywords(type.getTypeName(), allWords);
    }
  }

  private Type getType(List<Integer> vector) {
    Integer idOfType = vector.indexOf(Collections.max(vector)) + 1;
    return typeRepository.findById(idOfType).orElseThrow();
  }

  private void countWordsForFindingNewKeywords(String typeName, List<String> allWords) {

    Set<WordCount> wordCountsToSave = new HashSet<>();
    Map<String, Long> wordCounts =
        allWords.stream()
            .parallel()
            .collect(Collectors.groupingBy(word -> word, Collectors.counting()));

    wordCounts.forEach(
        (word, count) -> {
          Optional<WordCount> maybeWordCount =
              wordCountRepository.findByWordAndTypeName(word, typeName);
          if (maybeWordCount.isPresent()) {
            WordCount wordCount = maybeWordCount.get();
            wordCount.setCount(wordCount.getCount() + count);
            wordCount.setInGames(wordCount.getInGames() + 1);
            wordCountsToSave.add(wordCount);
          } else {
            WordCount newWordCount = new WordCount();
            newWordCount.setCount(count);
            newWordCount.setTypeName(typeName);
            newWordCount.setWord(word);
            newWordCount.setInGames(1);
            wordCountsToSave.add(newWordCount);
          }
        });
    wordCountRepository.saveAll(wordCountsToSave);
  }

  private void getIgnReview(Document doc, StringBuilder text) {
    Set<String> urls =
        doc
            .body()
            .select(
                "#__layout > div > div.c-layoutDefault_page > div.c-pageProductReviews-wrapper > div.c-pageProductReviews.u-grid.g-grid-container.g-outer-spacing-bottom-xxlarge > section > div.c-pageProductReviews_row.g-outer-spacing-bottom-xxlarge")
            .select(
                "div.c-siteReview_extra.u-grid.u-grid-gap-medium.g-inner-spacing-medium.o-border-thin-top.g-border-gray30 > a")
            .stream()
            .map(elem -> elem.attr("href"))
            .filter(url -> url.contains("ign") && url.contains("articles"))
            .collect(Collectors.toSet());

    urls.forEach(
        url -> {
          try {
            var newDoc = Jsoup.connect(url).get();
            var elems = newDoc.body().select("section.article-page");
            if (elems.size() < 1) {
              return;
            }
            text.append(elems.get(0).text());
          } catch (IOException ignored) {
          }
        });
  }
}
