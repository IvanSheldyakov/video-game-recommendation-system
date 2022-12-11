package com.example.demo.service;

import com.example.demo.repository.GameRepository;
import com.example.demo.repository.TypeRepository;
import com.example.demo.repository.WordCountRepository;
import com.example.demo.repository.WordRepository;
import com.example.demo.utils.Constants;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class PageParser extends Thread {


    private final BeanFactory beanFactory;

    private final String pageUrl;

    private final ExecutorService executor = Executors.newFixedThreadPool(2);

    public PageParser(int page, BeanFactory beanFactory) {
        if (page == 0) {
            pageUrl = Constants.zeroPage;
        } else {
            pageUrl = Constants.toPage + page;
        }
        this.beanFactory = beanFactory;
    }

    @Override
    public void run() {
        try {

            var doc = Jsoup.connect(pageUrl).get();
            var elements = doc.body().select("a.title");
            for (var elem : elements) {
                String gameUrl = elem.attr("href"); //https://www.metacritic.com/game/playstation-3/grand-theft-auto-iv/critic-reviews

                beanFactory.getBean(GameHandler.class,gameUrl);

                executor.submit(
                      beanFactory.getBean(GameHandler.class,Constants.mainUrl + gameUrl)
                );
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            executor.shutdown();
        }
    }

}
