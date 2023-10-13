package com.example.demo.service;

import javax.annotation.PreDestroy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.stereotype.Component;

@Component
public class Parser {

  private final BeanFactory beanFactory;
  private final KeywordsService keywordsService;

  private final WebDriver webDriver;

  public Parser(BeanFactory beanFactory, KeywordsService keywordsService) {
    this.beanFactory = beanFactory;
    this.keywordsService = keywordsService;
    this.webDriver = init();
  }

  public void parse() {
    try {
      for (int i = 3; i <= 3; i++) { // 6000
        PageParser parser = beanFactory.getBean(PageParser.class, i, webDriver);
        parser.parse();
        // keywordsService.update(); каждые 1000 игр
      }
      System.out.println("done");

    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  @PreDestroy
  private void clean() {
    webDriver.quit();
  }

  private WebDriver init() {
    System.setProperty("webdriver.edge.driver", "C:\\Users\\Public\\msedgedriver.exe");
    EdgeOptions options = new EdgeOptions();
    options.addArguments("--remote-allow-origins=*");
    return new EdgeDriver(options);
  }
}
