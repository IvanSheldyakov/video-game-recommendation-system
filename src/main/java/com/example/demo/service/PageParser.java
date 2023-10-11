package com.example.demo.service;

import com.example.demo.utils.Constants;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.BeanFactory;

public class PageParser {

  private final BeanFactory beanFactory;

  private final String pageUrl;

  private final WebDriver driver;

  // private final ExecutorService executor = Executors.newFixedThreadPool(4);

  public PageParser(int page, BeanFactory beanFactory, WebDriver webDriver) {
    pageUrl = Constants.TO_PAGE + page;
    this.driver = webDriver;
    this.beanFactory = beanFactory;
  }

  public void parse() throws IOException {
    /* try {
        Document doc = Jsoup.connect(pageUrl).get();
        var elements = doc.body()
                .select("div.c-finderProductCard")
                .select("a.c-finderProductCard_container");

        var gameHandlers =
                elements.stream()
                        .map(elem -> elem.attr("href"))
                        .map(gameUrl -> beanFactory.getBean(GameHandler.class, Constants.MAIN_URL + gameUrl, driver)
                        )
                        .toList();
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
    }*/

    driver.get("https://www.metacritic.com/game/the-legend-of-zelda-ocarina-of-time/");

    // Получите исходный код страницы после выполнения JavaScript
    String pageSource = driver.getPageSource();
    // Выведите исходный код страницы
    var doc = Jsoup.parse(pageSource);
    String el =
        doc.body()
            .select(
                "#__layout > div > div.c-layoutDefault_page > div.c-pageProductGame > div.c-pageProduct_row.g-grid-container.c-pageProductionDetails > div > div > div.c-productionDetailsGame_grid.u-grid > div.c-productionDetailsGame-summary.g-outer-spacing-bottom-small.g-container-rounded-small > p > span.c-productionDetailsGame_description.g-text-xsmall")
            .text()
            .trim();
    // System.out.println(el);

    System.out.println(
        Jsoup.connect(
                "https://www.metacritic.com/game/the-legend-of-zelda-ocarina-of-time/details/")
            .get()
            .select(
                "#__layout > div > div.c-layoutDefault_page > div.c-pageProductDetails.g-grid-container.g-outer-spacing-bottom-xxlarge > div.c-pageProductDetails_description.g-outer-spacing-bottom-xlarge")
            .text());

    // Закройте браузер и завершите сессию WebDriver

    // Document doc =
    // Jsoup.connect("https://www.metacritic.com/game/the-legend-of-zelda-ocarina-of-time/critic-reviews/").get();
    // Elements elements = doc.select(".criticScoreSummary");
    // System.out.println(doc);
    // #__layout > div > div.c-layoutDefault_page > div.c-pageProductReviews-wrapper >
    // div.c-pageProductReviews.u-grid.g-grid-container.g-outer-spacing-bottom-xxlarge > section >
    // div.c-pageProductReviews_row.g-outer-spacing-bottom-xxlarge > div:nth-child(1) > div >
    // div.c-siteReview_main.g-inner-spacing-medium > div:nth-child(2) > div

  }
}
