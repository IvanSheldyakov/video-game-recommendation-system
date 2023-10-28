package com.example.demo.service;

import javax.annotation.PreDestroy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
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
    //  webDriver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
  }

  public void parse() {
    try {
      for (int i = 1; i <= 10; i++) { // 6000
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
    // webDriver.quit();
  }

  private WebDriver init() {
    System.setProperty("webdriver.chrome.driver", "C:\\Users\\Public\\chromedriver.exe");
    ChromeOptions options = new ChromeOptions();
    options.addArguments("--remote-allow-origins=*");

    return null; // new ChromeDriver(options);
  }
}
