package com.example.demo.config;

import com.example.demo.repository.GameRepository;
import com.example.demo.repository.TypeRepository;
import com.example.demo.repository.WordCountRepository;
import com.example.demo.repository.WordRepository;
import com.example.demo.service.GameHandler;
import com.example.demo.service.PageParser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
@ComponentScan("com.example.demo")
@RequiredArgsConstructor
public class ParserConfig {

    private final GameRepository gameRepository;

    private final TypeRepository typeRepository;

    private final WordRepository wordRepository;

    private final WordCountRepository wordCountRepository;


    private final BeanFactory beanFactory;

    @Bean
    @Scope(scopeName = "prototype")
    public GameHandler gameHandler(String url){
        return new GameHandler(url,gameRepository,typeRepository,wordRepository,wordCountRepository);
    }

    @Bean
    @Scope(scopeName = "prototype")
    public PageParser pageParser(int page) {
        return new PageParser(page,beanFactory);
    }
}
