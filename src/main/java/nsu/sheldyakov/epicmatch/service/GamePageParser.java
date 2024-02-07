package nsu.sheldyakov.epicmatch.service;

import static nsu.sheldyakov.epicmatch.utils.Constants.DESCRIPTION;
import static nsu.sheldyakov.epicmatch.utils.Constants.SUMMARY_MOCK;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nsu.sheldyakov.epicmatch.model.GameData;
import nsu.sheldyakov.epicmatch.utils.Constants;
import nsu.sheldyakov.epicmatch.utils.MonthConverter;
import nsu.sheldyakov.epicmatch.utils.TextFormatter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class GamePageParser {

  private final WebDriver webDriver;
  private final RetryableWrapper retryableWrapper;

  public GameData getGameData(String gameUrl) {
    webDriver.get(gameUrl);
    Document gameDoc = Jsoup.parse(webDriver.getPageSource());

    String summary = getSummary(gameUrl);
    return GameData.builder()
        .name(getGameName(gameDoc))
        .score(getScore(gameDoc))
        .releaseDate(getDate(gameDoc))
        .rating(getRating(gameDoc))
        .genre(getGenre(gameDoc))
        .publisher(getPublisher(gameDoc))
        .platforms(getPlatforms(gameDoc))
        .summary(getSummary(gameUrl))
        .description(getGameTextDescription(gameUrl) + summary)
        .build();
  }

  private String getSummary(String gameUrl) {
    Document gameDetails = null;
    try {
      gameDetails =
          retryableWrapper.wrapInRetryable(
              () -> {
                return Jsoup.connect(gameUrl + Constants.TO_DETAILS).get();
              });

    } catch (Exception e) {
      log.error("Cant get summary from {}", gameUrl + Constants.TO_DETAILS);
      return SUMMARY_MOCK;
    }
    return TextFormatter.removeTextInBracketsAfterDot(getSummary(gameDetails));
  }

  private String getGameTextDescription(String gameUrl) {
    StringBuilder text = new StringBuilder();
    Document criticReviewsDoc = getCriticReviewsDoc(gameUrl);

    var elements =
        criticReviewsDoc
            .body()
            .select(
                "#__layout > div > div.c-layoutDefault_page > "
                    + "div.c-pageProductReviews-wrapper > "
                    + "div.c-pageProductReviews.u-grid.g-grid-container.g-outer-spacing-bottom-xxlarge > "
                    + "section > div.c-pageProductReviews_row.g-outer-spacing-bottom-xxlarge")
            .select(
                "div.c-siteReview.g-bg-gray10.u-grid.g-outer-spacing-bottom-large > div.c-siteReview_main.g-inner-spacing-medium")
            .select("div > span");

    for (var elem : elements) {
      text.append(elem.text()).append(" ");
    }

    getIgnReview(criticReviewsDoc, text);
    getEurogamerReview(criticReviewsDoc, text);
    return text.toString();
  }

  private Document getCriticReviewsDoc(String gameUrl) {
    webDriver.get(gameUrl + Constants.TO_CRITIC_REVIEWS);
    String pageSource = webDriver.getPageSource();
    return Jsoup.parse(pageSource);
  }

  private LocalDate getDate(Document gameDoc) {
    var elements =
        gameDoc
            .body()
            .select("#__layout > div > div.c-layoutDefault_page > div.c-pageProductGame")
            .select(
                "div.c-productHero_player-scoreInfo.u-grid.g-grid-container > "
                    + "div.c-productHero_score-container.u-flexbox.u-flexbox-column.g-bg-white > "
                    + "div.g-text-xsmall > span.u-text-uppercase");
    return elements.isEmpty() ? null : getReleaseDate(elements.get(0).text().trim());
  }

  private Integer getScore(Document gameDoc) {
    var elements =
        gameDoc
            .body()
            .select("#__layout > div > div.c-layoutDefault_page > div.c-pageProductGame")
            .select(
                "div.c-productHero_player-scoreInfo.u-grid.g-grid-container > "
                    + "div.c-productHero_score-container.u-flexbox.u-flexbox-column.g-bg-white > "
                    + "div.c-productHero_scoreInfo.g-inner-spacing-top-medium.g-outer-spacing-bottom-medium.g-outer-spacing-top-medium > "
                    + "div:nth-child(1) > div > div.c-productScoreInfo_scoreContent.u-flexbox.u-flexbox-alignCenter.u-flexbox-justifyFlexEnd.g-width-100.u-flexbox-nowrap > "
                    + "div.c-productScoreInfo_scoreNumber.u-float-right > div > div > span");
    return elements.isEmpty() ? null : Integer.parseInt(elements.get(0).text().trim());
  }

  private String getGameName(Document gameDoc) {
    return gameDoc
        .body()
        .select("#__layout > div > div.c-layoutDefault_page > div.c-pageProductGame")
        .select(
            "div.c-productHero_player-scoreInfo.u-grid.g-grid-container > "
                + "div.c-productHero_score-container.u-flexbox.u-flexbox-column.g-bg-white > "
                + "div.c-productHero_title.g-inner-spacing-bottom-medium.g-outer-spacing-top-medium > "
                + "div")
        .get(0)
        .text()
        .trim();
  }

  private Set<String> getPlatforms(Document gameDoc) {
    var platforms =
        gameDoc
            .body()
            .select(
                "#__layout > div > div.c-layoutDefault_page > "
                    + "div.c-pageProductGame > div.c-pageProduct_row.g-grid-container.c-pageProductionDetails > "
                    + "div > div > div.c-productionDetailsGame_grid.u-grid > div.c-gameDetails")
            .select("div.c-gameDetails_Platforms.u-flexbox.u-flexbox-row")
            .select("li.c-gameDetails_listItem");
    return platforms.stream().map(Element::text).collect(Collectors.toSet());
  }

  private String getPublisher(Document gameDoc) {
    return gameDoc
        .body()
        .select(
            "#__layout > div > div.c-layoutDefault_page > div.c-pageProductGame > "
                + "div.c-pageProduct_row.g-grid-container.c-pageProductionDetails > div > div > "
                + "div.c-productionDetailsGame_grid.u-grid > div.c-gameDetails")
        .select(
            "div.c-gameDetails_Distributor.u-flexbox.u-flexbox-row > "
                + "span.g-outer-spacing-left-medium-fluid.g-color-gray70.u-block")
        .text();
  }

  private String getRating(Document gameDoc) {
    var elements =
        gameDoc
            .body()
            .select(
                "#__layout > div > div.c-layoutDefault_page > div.c-pageProductGame > "
                    + "div.c-pageProduct_row.g-grid-container.c-pageProductionDetails > div > div > "
                    + "div.c-productionDetailsGame_grid.u-grid > "
                    + "div.c-productionDetailsGame-summary.g-outer-spacing-bottom-small.g-container-rounded-small > "
                    + "div > div > div.c-productionDetailsGame_esrb_title.u-inline-block.g-outer-spacing-left-medium-fluid");

    return elements.isEmpty() ? null : elements.text().trim();
  }

  private String getGenre(Document gameDoc) {
    return gameDoc
        .body()
        .select(
            "#__layout > div > div.c-layoutDefault_page > div.c-pageProductGame > "
                + "div.c-pageProduct_row.g-grid-container.c-pageProductionDetails > div > div > "
                + "div.c-productionDetailsGame_grid.u-grid > div.c-gameDetails > "
                + "div.c-gameDetails_sectionContainer.u-flexbox.u-flexbox-row.u-flexbox-alignBaseline > "
                + "ul > li > div > a > span")
        .text()
        .trim();
  }

  private String getSummary(Document gameDoc) {
    String description =
        gameDoc
            .body()
            .select(
                "#__layout > div > div.c-layoutDefault_page > "
                    + "div.c-pageProductDetails.g-grid-container.g-outer-spacing-bottom-xxlarge > "
                    + "div.c-pageProductDetails_description.g-outer-spacing-bottom-xlarge")
            .text()
            .trim();
    int minLength = DESCRIPTION.length();
    if (description.length() < minLength) {
      return SUMMARY_MOCK;
    }
    return description.substring(minLength);
  }

  private LocalDate getReleaseDate(String date) {
    String[] parts = date.split(",");
    String[] monthAndDay = parts[0].split(" ");
    int year = Integer.parseInt(parts[1].trim());
    int month = MonthConverter.convert(monthAndDay[0]);
    int day = Integer.parseInt(monthAndDay[1]);
    return LocalDate.of(year, month, day);
  }

  private void getIgnReview(Document doc, StringBuilder text) {
    Set<String> urls =
        doc
            .body()
            .select(
                "#__layout > div > div.c-layoutDefault_page > div.c-pageProductReviews-wrapper > div.c-pageProductReviews.u-grid.g-grid-container.g-outer-spacing-bottom-xxlarge > section > div.c-pageProductReviews_row.g-outer-spacing-bottom-xxlarge")
            .select(
                "div.c-siteReview_extra.u-grid.u-grid-gap-medium.g-inner-spacing-medium.o-border-thin-top.g-border-gray30 > a")
            .stream()
            .map(elem -> elem.attr("href"))
            .filter(url -> url.contains("ign") && url.contains("articles"))
            .collect(Collectors.toSet());

    urls.forEach(
        url -> {
          try {
            var newDoc = Jsoup.connect(url).get();
            var elems = newDoc.body().select("section.article-page");
            if (elems.size() < 1) {
              return;
            }
            text.append(elems.get(0).text());
            log.info("Visited ign url={}", url);
          } catch (IOException ignored) {
          }
        });
  }

  private void getEurogamerReview(Document doc, StringBuilder text) {
    Set<String> urls =
        doc
            .body()
            .select(
                "#__layout > div > div.c-layoutDefault_page > "
                    + "div.c-pageProductReviews-wrapper > "
                    + "div.c-pageProductReviews.u-grid.g-grid-container.g-outer-spacing-bottom-xxlarge > "
                    + "section > div.c-pageProductReviews_row.g-outer-spacing-bottom-xxlarge")
            .select(
                "div.c-siteReview_extra.u-grid.u-grid-gap-medium.g-inner-spacing-medium.o-border-thin-top.g-border-gray30 > a")
            .stream()
            .map(elem -> elem.attr("href"))
            .filter(url -> url.contains("eurogamer.net"))
            .collect(Collectors.toSet());

    urls.forEach(
        url -> {
          try {
            var newDoc = Jsoup.connect(url).get();
            var elems =
                newDoc
                    .body()
                    .select(
                        "#content_above > " + "div.page_content > article > div > div > section");
            if (elems.size() < 1) {
              return;
            }
            text.append(elems.get(0).text());
            log.info("Visited euro url={}", url);
          } catch (IOException ignored) {
          }
        });
  }
}
