package com.example.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Parser {

    private final BeanFactory beanFactory;

    public void parse() {
        try {
            for (int i = 85; i <= 130; i++) { //198
                PageParser parser = beanFactory.getBean(PageParser.class, i);
                parser.parse();

            }
            System.out.println("done");

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
