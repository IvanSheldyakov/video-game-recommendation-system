package com.example.demo.service;

import com.example.demo.domain.*;
import com.example.demo.repository.*;
import com.example.demo.utils.Constants;
import com.example.demo.utils.MonthConverter;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


@RequiredArgsConstructor
public class GameHandler extends Thread {

    private final String gameUrl;
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

    private final static Object LOCK = new Object();

    @Override
    @Transactional
    public void run() {
        try {
            StringBuilder text = new StringBuilder();
            var doc = Jsoup.connect(gameUrl + Constants.toCriticReviews).get();
            var elements = doc.body().select("div.review_body");
            for (var elem : elements) {
                text.append(elem.text()).append(" ");
            }
            getIgnReview(doc, text);
            var map = wordsFinder.find(text.toString());

            Game game = gameRepository.save(fillGameInfo(doc));

            analyzeTypeOfGame(map, game);
            System.out.println(game);


        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private Game fillGameInfo(Document doc) {
        String gameName = doc.body()
                .select("div.product_title").get(0)
                .select("a.hover_none").get(0).text().trim();

        Integer score = Integer.parseInt(doc.body().select("a.metascore_anchor").get(0).text().trim());
        String date = doc.body().select("div.product_data").get(0).select("span.data").get(1).text().trim();

        Rating rating = null;
        String summary = null;
        String trailerUrl = null;
        Set<Genre> genres = new HashSet<>();
        try {
            var gameDoc = Jsoup.connect(gameUrl).get();
            rating = getRating(gameDoc);
            summary = getSummary(gameDoc);
            genres = getGenres(gameDoc);
            trailerUrl = getTrailerUrl(gameDoc);
        } catch (IOException ignored) {
        }

        Game game = new Game();
        game.setName(gameName);
        game.getPlatforms().add(getPlatform(doc));
        game.getGenres().addAll(genres);
        game.setScore(score);
        game.setReleaseDate(getReleaseDate(date));
        game.setPublisher(getPublisher(doc));
        game.setRating(rating);
        game.setSummary(summary);
        game.setTrailerLink(trailerUrl);
        return game;
    }

    private Platform getPlatform(Document doc) {
        String platform = doc.body()
                .select("div.product_title").get(0)
                .select("span.platform").get(0).text().trim();
        return platformRepository.findByPlatform(platform).orElse(new Platform(platform));
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
        String publisher = doc.body()
                .select("div.product_data").get(0)
                .select("span.data").get(0).text().trim();
        return publisherRepository.findByPublisher(publisher).orElse(new Publisher(publisher));
    }

    private Rating getRating(Document doc) {
        var el = doc.body().select("ul.summary_details")
                .select("li.summary_detail.product_rating")
                .select("span.data");

        if (el.size() < 1) {
            return null;
        } else {
            String rating = el.get(0).text().trim();
            return ratingRepository.findByRating(rating).orElse(new Rating(rating));
        }
    }

    private Set<Genre> getGenres(Document doc) {
        Elements elements = doc.body()
                .select("div.summary_wrap")
                .select("div.section.product_details")
                .select("div.details.side_details")
                .select("li.summary_detail.product_genre").select("span.data");
        return elements.stream()
                .map(Element::text)
                .map(genreName -> genreRepository.findByGenre(genreName).orElse(new Genre(genreName)))
                .collect(Collectors.toSet());
    }

    private String getSummary(Document doc) {
        return doc.body()
                .select("div.summary_wrap")
                .select("div.section.product_details")
                .select("div.details.main_details")
                .select("span.blurb.blurb_collapsed").text().trim();
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
        List<String> allWords = map.values().stream()
                .flatMap(List::stream)
                .toList();

        List<Type> types = typeRepository.findAll();
        List<Integer> vector = types.stream()
                .map(type -> {
                    List<String> wordList = wordRepository.findWordsByType(type).stream().parallel()
                            .map(Word::getWord)
                            .toList();
                    return (int) allWords.stream().parallel()
                            .filter(wordList::contains)
                            .count();
                })
                .toList();

        game.setVector(vector);
        Type type = getType(vector);
        game.setType(type);
        if (findNewKeywords) {
            countWordsForFindingNewKeywords(type.getTypeName(), allWords, game);
        }
    }

    private Type getType(List<Integer> vector) {
        Integer idOfType = vector.indexOf(Collections.max(vector)) + 1;
        return typeRepository.findById(idOfType).orElseThrow();

    }

    private void countWordsForFindingNewKeywords(String typeName, List<String> allWords, Game game) {

        Map<String, Long> wordCounts = allWords.stream().parallel()
                .collect(Collectors.groupingBy(
                        word -> word,
                        Collectors.counting()
                ));

        wordCounts.forEach((word, count) -> {
            if (wordCountRepository.existsByWordAndTypeName(word, typeName)) {
                WordCount wordCount = wordCountRepository.findByWordAndTypeName(word, typeName);
                wordCount.setCount(wordCount.getCount() + count);
                wordCount.getGameIds().add(game.getId());
                wordCountRepository.save(wordCount);
            } else {
                WordCount newWordCount = new WordCount();
                newWordCount.setCount(count);
                newWordCount.setTypeName(typeName);
                newWordCount.setWord(word);
                newWordCount.getGameIds().add(game.getId());
                wordCountRepository.save(newWordCount);
            }
        });
    }

    private void getIgnReview(Document doc, StringBuilder text) {
        Set<String> urls = doc.body().select("a.external").stream()
                .map(elem -> elem.attr("href"))
                .filter(url -> url.contains("ign") && url.contains("articles"))
                .collect(Collectors.toSet());

        urls.forEach(url -> {
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
