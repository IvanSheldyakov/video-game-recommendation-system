package com.example.demo.service;

import com.example.demo.repository.GameRepository;
import com.example.demo.repository.TypeRepository;
import com.example.demo.repository.WordRepository;
import com.example.demo.utils.Constants;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Configurable;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configurable
public class PageParser extends Thread {


    private final String pageUrl;

    private final GameRepository gameRepository;


    private final TypeRepository typeRepository;


    private final WordRepository wordRepository;

    private final ExecutorService executor = Executors.newFixedThreadPool(8);

    public PageParser(int page, GameRepository gameRepository, TypeRepository typeRepository, WordRepository wordRepository) {
        if (page == 0) {
            pageUrl = Constants.zeroPage;
        } else {
            pageUrl = Constants.toPage + page;
        }
        this.gameRepository = gameRepository;
        this.typeRepository = typeRepository;
        this.wordRepository = wordRepository;
    }

    @Override
    public void run() {
        try {
            var doc = Jsoup.connect(pageUrl).get();
            var elements = doc.body().select("a.title");
            for (var elem : elements) {
                String gameUrl = elem.attr("href"); //https://www.metacritic.com/game/playstation-3/grand-theft-auto-iv/critic-reviews


                        GameHandler handler = new GameHandler(
                                Constants.mainUrl + gameUrl,
                                gameRepository,
                                typeRepository,
                                wordRepository
                        );
                        handler.start();
                        handler.join();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            executor.shutdown();
        }
    }

}
