package com.example.demo.service;

import com.example.demo.repository.TypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Parser {

    private final BeanFactory beanFactory;


    //  private final ExecutorService executor = Executors.newFixedThreadPool(2);

    public void parse() {
        try {
            for (int i = 6; i <= 30; i++) {
                PageParser parser = beanFactory.getBean(PageParser.class, i);

                parser.run();

            }
            System.out.println("done");

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
