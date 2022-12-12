package com.example.demo.service;

import com.example.demo.domain.Game;
import com.example.demo.domain.Type;
import com.example.demo.domain.Word;
import com.example.demo.domain.WordCount;
import com.example.demo.repository.GameRepository;
import com.example.demo.repository.TypeRepository;
import com.example.demo.repository.WordCountRepository;
import com.example.demo.repository.WordRepository;
import com.example.demo.utils.Constants;
import com.example.demo.utils.MonthConverter;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;


@RequiredArgsConstructor
public class GameHandler extends Thread{

    private final String gameUrl;


    private final GameRepository gameRepository;

    private final TypeRepository typeRepository;

    private final WordRepository wordRepository;


    private final WordCountRepository wordCountRepository;

    private final WordsFinder wordsFinder = new WordsFinder();

    private boolean analyze = true;



    @Override
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


            Game game = fillGameInfo(doc);
            game.setVector(countWords(map));
            System.out.println(game);
            gameRepository.save(game);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private Game fillGameInfo(Document doc) throws IOException {
        String gameName = doc.body().select("div.product_title").get(0).select("a.hover_none").get(0).text();
        String platform = doc.body().select("div.product_title").get(0).select("span.platform").get(0).text();
        Integer score = Integer.parseInt(doc.body().select("a.metascore_anchor").get(0).text());
        String publisher = doc.body().select("div.product_data").get(0).select("span.data").get(0).text();
        String date = doc.body().select("div.product_data").get(0).select("span.data").get(1).text();
        String rating = getRating();

        Game game = new Game();
        game.setName(gameName);
        game.setPlatform(platform);
        game.setScore(score);
        game.setReleaseDate(getReleaseDate(date));
        game.setPublisher(publisher);
        game.setRating(rating);
        return game;
    }

    private String getRating() throws IOException {
        var doc = Jsoup.connect(gameUrl).get();
        var el = doc.body().select("ul.summary_details")
                .select("li.summary_detail.product_rating")
                .select("span.data");

        if (el.size() < 1) {
            return null;
        } else {
            return el.get(0).text();
        }

    }

    private LocalDate getReleaseDate(String date) {
        String[] parts = date.split(",");
        String[] monthAndDay = parts[0].split(" ");
        int year = Integer.parseInt(parts[1].trim());
        int month = MonthConverter.convert(monthAndDay[0]);
        int day = Integer.parseInt(monthAndDay[1]);
        return LocalDate.of(year, month, day);


    }

    private List<Integer> countWords(HashMap<String, List<String>> map) {
        List<String> allWords = new ArrayList<>();
        map.values().forEach(allWords::addAll);
        List<Type> types = typeRepository.findAll();
        List<Integer> vector = new ArrayList<>();
        for (Type type : types) {
            int count = 0;
            List<String> wordList = wordRepository.findWordsByType(type).stream().map(Word::getWord).toList();
            for (String kWord : wordList) {
                for (String word : allWords) {
                    if (kWord.equals(word)) {
                        count++;
                    }
                }
            }
            vector.add(count);
        }

        if (analyze) {
            Integer idOfType = vector.indexOf(Collections.max(vector)) + 1;
            var type = typeRepository.findById(idOfType);
            String typeName = type.get().getTypeName();
            for (String word : allWords) {
                if (wordCountRepository.existsByWordAndTypeName(word, typeName)) {
                    WordCount wordCount = wordCountRepository.findByWordAndTypeName(word, typeName);
                    long count = wordCount.getCount() + 1;
                    wordCount.setCount(count);
                    wordCountRepository.save(wordCount);
                } else {
                    WordCount newWordCount = new WordCount();
                    newWordCount.setCount(1);
                    newWordCount.setTypeName(typeName);
                    newWordCount.setWord(word);
                    wordCountRepository.save(newWordCount);
                }
            }
        }

        return vector;

    }

    private void getIgnReview(Document doc, StringBuilder text) {
        Set<String> urls = new HashSet<>();
        var elements = doc.body().select("a.external");
        for (var elem : elements) {
            String url = elem.attr("href");
            if (url.contains("ign") && url.contains("articles") && !urls.contains(url)) {
                urls.add(url);
                try {
                    var newDoc = Jsoup.connect(url).get();
                    var elems = newDoc.body().select("section.article-page");
                    if (elems.size() < 1) {
                        continue;
                    }
                    text.append(elems.get(0).text());
                } catch (IOException ignored) {

                }

            }
        }
    }

}
