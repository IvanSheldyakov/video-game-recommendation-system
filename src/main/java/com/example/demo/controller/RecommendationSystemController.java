package com.example.demo.controller;

import com.example.demo.domain.Game;
import com.example.demo.mapper.GameMapper;
import com.example.demo.model.GameTypeBlock;
import com.example.demo.model.SearchCriteria;
import com.example.demo.repository.GameRepository;
import com.example.demo.service.GameInfoService;
import com.example.demo.utils.Constants;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
    List<GameTypeBlock> gameTypes = gameInfoService.getGameTypes();

    model.addAttribute("colors", Constants.colors);
    model.addAttribute("platforms", platforms);
    model.addAttribute("genres", genres);
    model.addAttribute("publishers", publishers);
    model.addAttribute("customBlocks", gameTypes);
    model.addAttribute("searchCriteria", new SearchCriteria());

    return "start_page";
  }

  @PostMapping("/submit")
  public String handleUserChoice(SearchCriteria searchCriteria) {
    // Обработка данных searchCriteria

    System.out.println(searchCriteria);
    System.out.println(
        gameRepository.findGameInfoByFilter(searchCriteria).stream()
            .map((Game game) -> GameMapper.toGameInfo(game, searchCriteria))
            .collect(Collectors.toList()));
    return "redirect:/start"; // или другой путь, куда вы хотите перенаправить пользователя
  }
}
