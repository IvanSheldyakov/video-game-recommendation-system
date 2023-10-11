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

  // private final ExecutorService executor = Executors.newFixedThreadPool(4);

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
    } finally {
      // executor.shutdown();
    }

    /* driver.get("https://www.metacritic.com/game/the-elder-scrolls-v-skyrim/critic-reviews/");

        // Получите исходный код страницы после выполнения JavaScript
        String pageSource = driver.getPageSource();
        // Выведите исходный код страницы
        var doc = Jsoup.parse(pageSource);
        var el = doc.body().select("#__layout > div > div.c-layoutDefault_page > div.c-pageProductReviews-wrapper > div.c-pageProductReviews.u-grid.g-grid-container.g-outer-spacing-bottom-xxlarge > section > div.c-pageProductReviews_row.g-outer-spacing-bottom-xxlarge").select("div.c-siteReview_extra.u-grid.u-grid-gap-medium.g-inner-spacing-medium.o-border-thin-top.g-border-gray30 > a").stream().map(elem -> elem.attr("href")).toList();


        System.out.println(el);

    /*System.out.println(
        Jsoup.connect(
                "https://www.metacritic.com/game/the-elder-scrolls-v-skyrim/details/")
            .get()
            .select("#__layout > div > div.c-layoutDefault_page > div.c-pageProductDetails.g-grid-container.g-outer-spacing-bottom-xxlarge > div.c-pageProductDetails_overview.g-outer-spacing-bottom-xlarge > div.c-gameDetails").select("div.c-gameDetails_Platforms.u-flexbox.u-flexbox-row")
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
        */

  }
}
