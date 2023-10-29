package com.example.demo.controller;

import com.example.demo.mapper.GameMapper;
import com.example.demo.model.GameInfo;
import com.example.demo.model.GameTypeBlock;
import com.example.demo.model.SearchCriteria;
import com.example.demo.repository.GameRepository;
import com.example.demo.service.GameInfoService;
import com.example.demo.utils.Constants;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping
@RequiredArgsConstructor
public class RecommendationSystemController {

  private final GameInfoService gameInfoService;
  private final GameRepository gameRepository;

  @GetMapping("/start")
  public String showStartPage(Model model) {

    List<String> platforms = gameInfoService.getPlatforms();
    List<String> genres = gameInfoService.getGenres();
    List<String> publishers = gameInfoService.getPublishers();
    List<String> ratings = gameInfoService.getRatings();
    List<GameTypeBlock> gameTypes = gameInfoService.getGameTypes();

    model.addAttribute("colors", Constants.colors);
    model.addAttribute("platforms", platforms);
    model.addAttribute("genres", genres);
    model.addAttribute("publishers", publishers);
    model.addAttribute("ratings", ratings);
    model.addAttribute("customBlocks", gameTypes);
    model.addAttribute("searchCriteria", new SearchCriteria());

    return "start_page";
  }

  @PostMapping("/submit")
  public String handleSubmit(SearchCriteria searchCriteria, HttpSession session) {
    session.setAttribute("searchCriteria", searchCriteria);
    return "redirect:/results";
  }

  @GetMapping("/results")
  public String handleResults(
      @RequestParam(defaultValue = "0") int page, HttpSession session, Model model) {

    SearchCriteria searchCriteria = (SearchCriteria) session.getAttribute("searchCriteria");
    if (searchCriteria == null) {
      throw new RuntimeException("Search criteria is null");
    }

    String vector = convertListToString(searchCriteria.getCustomValues());
    System.out.println(vector);

    var count =
        gameRepository.countGameInfoByFilter(
            searchCriteria.getPlatform(),
            searchCriteria.getMinScore(),
            searchCriteria.getGenre(),
            searchCriteria.getPublisher(),
            searchCriteria.getRating(),
            searchCriteria.getReleaseStartDate(),
            searchCriteria.getReleaseStartDate());

    int totalPages = (int) Math.ceil((double) count / 10);

    int offset = page * 10;

    var gamePage =
        gameRepository.findGameInfoByFilter(
            searchCriteria.getPlatform(),
            searchCriteria.getMinScore(),
            searchCriteria.getGenre(),
            searchCriteria.getPublisher(),
            searchCriteria.getRating(),
            searchCriteria.getReleaseStartDate(),
            searchCriteria.getReleaseStartDate(),
            vector,
            10,
            offset);

    List<GameInfo> games = gamePage.stream().map(GameMapper::toGameInfo).toList();

    model.addAttribute("games", games);
    model.addAttribute("currentPage", page);
    model.addAttribute(
        "pageNumbers", IntStream.range(0, totalPages).boxed().collect(Collectors.toList()));

    return "game_info_page";
  }

  public static String convertListToString(List<String> list) {
    return String.join(",", list);
  }
}
