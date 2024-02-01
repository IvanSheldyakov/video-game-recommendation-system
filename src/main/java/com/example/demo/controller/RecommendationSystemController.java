package com.example.demo.controller;

import com.example.demo.model.GameTypeBlock;
import com.example.demo.model.Page;
import com.example.demo.model.SearchCriteria;
import com.example.demo.service.GameInfoService;
import com.example.demo.utils.Constants;
import java.util.List;
import java.util.stream.IntStream;
import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping
@RequiredArgsConstructor
public class RecommendationSystemController {

  private final GameInfoService gameInfoService;

  @GetMapping("/start")
  public String showStartPage(Model model) {

    List<String> platforms = gameInfoService.getPlatforms();
    List<String> genres = gameInfoService.getGenres();
    List<String> publishers = gameInfoService.getPublishers();
    List<String> ratings = gameInfoService.getRatings();
    List<GameTypeBlock> gameTypes = gameInfoService.getGameTypes();

    model.addAttribute("colors", Constants.COLORS);
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
      throw new IllegalArgumentException("Search criteria is null");
    }

    Page gameInfoPage = gameInfoService.getPageOfGameInfos(searchCriteria, page);

    List<Integer> pageNumbers = IntStream.range(0, gameInfoPage.totalPages()).boxed().toList();

    model.addAttribute("games", gameInfoPage.gameInfos());
    model.addAttribute("currentPage", page);
    model.addAttribute("pageNumbers", pageNumbers);

    return "game_info_page";
  }
}
