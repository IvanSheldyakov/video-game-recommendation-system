package com.example.demo.service;

import com.example.demo.utils.Constants;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.BeanFactory;

public class PageParser {

  private final BeanFactory beanFactory;

  private final String pageUrl;

  private final WebDriver driver;

  public PageParser(int page, BeanFactory beanFactory, WebDriver webDriver) {
    pageUrl = Constants.TO_PAGE + page;
    this.driver = webDriver;
    this.beanFactory = beanFactory;
  }

  public void parse() {
    try {
      Document doc = Jsoup.connect(pageUrl).get();
      var elements =
          doc.body().select("div.c-finderProductCard").select("a.c-finderProductCard_container");

      var gameHandlers =
          elements.stream()
              .map(elem -> elem.attr("href"))
              .map(
                  gameUrl ->
                      beanFactory.getBean(GameHandler.class, Constants.MAIN_URL + gameUrl, driver))
              .toList();
      for (GameHandler handler : gameHandlers) {
        handler.run();
        handler.join();
      }

    } catch (IOException e) {
      System.out.println(e.getMessage());
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
