package com.example.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
public class Parser {

    private final BeanFactory beanFactory;

    private final ExecutorService executor = Executors.newFixedThreadPool(2);

    public void parse() {
        try {
            for (int i = 11; i <= 198; i++) {
                PageParser parser = beanFactory.getBean(PageParser.class, i);

                parser.start();
                parser.join();
                if (i == 20) {
                    //System.out.println("done");
                    break;
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            executor.shutdown();
        }
    }
}
