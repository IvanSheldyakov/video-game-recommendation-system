package com.example.demo.service;

import com.example.demo.utils.Constants;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.BeanFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class PageParser {

    private final BeanFactory beanFactory;

    private final String pageUrl;

    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    public PageParser(int page, BeanFactory beanFactory) {
        if (page == 0) {
            pageUrl = Constants.zeroPage;
        } else {
            pageUrl = Constants.toPage + page;
        }
        this.beanFactory = beanFactory;
    }


    public void parse() {
        List<Future<?>> futures = new ArrayList<>();
        try {

            var doc = Jsoup.connect(pageUrl).get();
            var elements = doc.body().select("a.title");
            for (var elem : elements) {
                String gameUrl = elem.attr("href"); //https://www.metacritic.com/game/playstation-3/grand-theft-auto-iv/critic-reviews

                beanFactory.getBean(GameHandler.class, gameUrl);

                GameHandler gameHandler = beanFactory.getBean(GameHandler.class, Constants.mainUrl + gameUrl);
                Future<?> future = executor.submit(gameHandler);
                futures.add(future);

            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            futures.forEach((x) -> {
                try {
                    Object o = x.get();
                } catch (InterruptedException | ExecutionException e) {
                    System.out.println(e.getMessage());
                }
            });
            executor.shutdown();
        }
    }

}
