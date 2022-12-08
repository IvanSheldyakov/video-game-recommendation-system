package com.example.demo.service;

import com.example.demo.domain.Game;
import com.example.demo.domain.Type;
import com.example.demo.domain.Word;
import com.example.demo.repository.GameRepository;
import com.example.demo.repository.TypeRepository;
import com.example.demo.repository.WordRepository;
import com.example.demo.utils.MonthConverter;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Configurable;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@Configurable
@RequiredArgsConstructor
public class GameHandler extends Thread {

    private final String gameUrl;


    private final GameRepository gameRepository;


    private final TypeRepository typeRepository;


    private final WordRepository wordRepository;

    private final WordsFinder wordsFinder = new WordsFinder();


    @Override
    public void run() {
        try {
            StringBuilder text = new StringBuilder();
            var doc = Jsoup.connect(gameUrl).get();
            var elements = doc.body().select("div.review_body");
            for (var elem : elements) {
                text.append(elem.text()).append(" ");
            }
            getIgnReview(doc, text);

            var map = wordsFinder.find(text.toString());

            String gameName = doc.body().select("div.product_title").get(0).select("a.hover_none").get(0).text();
            String platform = doc.body().select("div.product_title").get(0).select("span.platform").get(0).text();
            Integer score = Integer.parseInt(doc.body().select("a.metascore_anchor").get(0).text());
            String publisher = doc.body().select("div.product_data").get(0).select("span.data").get(0).text();
            String date = doc.body().select("div.product_data").get(0).select("span.data").get(1).text();


            Game game = new Game();
            game.setName(gameName);
            game.setPlatform(platform);
            game.setScore(score);
            game.setVector(countWords(map));
            game.setReleaseDate(getReleaseDate(date));
            game.setPublisher(publisher);
            System.out.println(game);
            //gameRepository.save(game);


        } catch (IOException e) {
            throw new RuntimeException(e);
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
        return vector;

    }

    private void getIgnReview(Document doc, StringBuilder text) {
        Set<String> urls = new HashSet<>();
        var elements = doc.body().select("a.external");
        for (var elem : elements) {
            String url = elem.attr("href");
            if (url.contains("ign") && url.contains("articles") && !urls.contains(url)) {
                System.out.println(url);
                urls.add(url);
                try {
                    var newDoc = Jsoup.connect(url).get();
                    var elems = newDoc.body().select("section.article-page");
                    text.append(elems.get(0).text());
                    System.out.println(elems.get(0).text());
                } catch (IOException ignored) {

                }

            }
        }
    }

}
