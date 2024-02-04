package nsu.sheldyakov.epicmatch.controller;

import lombok.RequiredArgsConstructor;
import nsu.sheldyakov.epicmatch.service.MetacriticGameInfoCollector;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/collector")
@RequiredArgsConstructor
public class GameInfoCollectorController {

  private final MetacriticGameInfoCollector metacriticGameInfoCollector;

  @PostMapping("/start")
  public void start(
      @RequestParam("firstPageNumber") int firstPageNumber,
      @RequestParam("lastPageNumber") int lastPageNumber) {
    metacriticGameInfoCollector.collect(firstPageNumber, lastPageNumber);
  }
}
