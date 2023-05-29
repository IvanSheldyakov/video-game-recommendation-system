package com.example.demo.service;

import com.example.demo.utils.Constants;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.BeanFactory;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class PageParser {

    private final BeanFactory beanFactory;

    private final String pageUrl;

   // private final ExecutorService executor = Executors.newFixedThreadPool(4);

    public PageParser(int page, BeanFactory beanFactory) {
        if (page == 0) {
            pageUrl = Constants.zeroPage;
        } else {
            pageUrl = Constants.toPage + page;
        }
        this.beanFactory = beanFactory;
    }


    public void parse() {

        try {
            var doc = Jsoup.connect(pageUrl).get();
            var elements = doc.body().select("a.title");

            var gameHandlers = elements.stream()
                    .map(elem -> elem.attr("href"))
                    .map(gameUrl -> beanFactory.getBean(GameHandler.class, Constants.mainUrl + gameUrl)).toList();
            for (GameHandler handler : gameHandlers) {
                handler.run();
                handler.join();
            }


        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
           // executor.shutdown();
        }
    }


}
