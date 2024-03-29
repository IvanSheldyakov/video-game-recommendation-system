package com.example.demo.config;

import com.example.demo.repository.*;
import com.example.demo.service.GameHandler;
import com.example.demo.service.PageParser;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.WebDriver;
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
  private final GenreRepository genreRepository;
  private final PlatformRepository platformRepository;
  private final PublisherRepository publisherRepository;
  private final RatingRepository ratingRepository;
  private final BeanFactory beanFactory;

  @Bean
  @Scope(scopeName = "prototype")
  public GameHandler gameHandler(String url, WebDriver driver) {
    return new GameHandler(
        url,
        driver,
        gameRepository,
        typeRepository,
        wordRepository,
        wordCountRepository,
        genreRepository,
        platformRepository,
        publisherRepository,
        ratingRepository);
  }

  @Bean
  @Scope(scopeName = "prototype")
  public PageParser pageParser(int page, WebDriver webDriver) {
    return new PageParser(page, beanFactory, webDriver);
  }
}
