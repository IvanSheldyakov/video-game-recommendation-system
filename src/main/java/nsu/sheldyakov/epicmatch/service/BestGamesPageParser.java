package nsu.sheldyakov.epicmatch.service;

import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import nsu.sheldyakov.epicmatch.utils.Constants;
import org.jsoup.Jsoup;
import org.jsoup.UncheckedIOException;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BestGamesPageParser {

  public List<String> getGamePageUrls(int pageNumber) {
    try {
      String pageUrl = Constants.TO_PAGE + pageNumber;
      Document doc = Jsoup.connect(pageUrl).get();
      var urlBlocks =
          doc.body().select("div.c-finderProductCard").select("a.c-finderProductCard_container");

      return urlBlocks.stream().map(elem -> elem.attr("href")).toList();

    } catch (IOException e) {
      log.error(e.getMessage() + " pageNumber={}", pageNumber);
      throw new UncheckedIOException(e);
    }
  }
}
