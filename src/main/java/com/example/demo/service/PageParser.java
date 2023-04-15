package com.example.demo.service;

import com.example.demo.utils.Constants;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.BeanFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;


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
        List<Future<?>> futures;
        try {
            var doc = Jsoup.connect(pageUrl).get();
            var elements = doc.body().select("a.title");

            futures = elements.stream()
                    .map(elem -> elem.attr("href"))
                    .map(gameUrl -> beanFactory.getBean(GameHandler.class, Constants.mainUrl + gameUrl))
                    .map(executor::submit)
                    .collect(Collectors.toList());

            // Ожидание завершения всех задач и обработка исключений
            futures.forEach(future -> {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    System.out.println(e.getMessage());
                }
            });

        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            executor.shutdown();
        }
    }


}
