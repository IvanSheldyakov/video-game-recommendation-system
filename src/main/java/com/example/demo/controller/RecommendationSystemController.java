package com.example.demo.controller;

import com.example.demo.model.SearchCriteria;
import com.example.demo.service.GameInfoService;
import com.example.demo.utils.Constants;
import java.util.List;
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

  @GetMapping("/start")
  public String showPlatform(Model model) {
    List<String> platforms = gameInfoService.getPlatforms();
    List<String> genres = gameInfoService.getGenres();
    List<String> publishers = gameInfoService.getPublishers();

    model.addAttribute("colors", Constants.colors);
    model.addAttribute("platforms", platforms);
    model.addAttribute("genres", genres);
    model.addAttribute("publishers", publishers);
    model.addAttribute("searchCriteria", new SearchCriteria());
    model.addAttribute("customBlocks", gameInfoService.getGameTypes());
    return "start_page";
  }

  @PostMapping("/submit")
  public String handleSubmit(SearchCriteria searchCriteria) {
    // Обработка данных searchCriteria
    // ...
    System.out.println(searchCriteria);
    return "redirect:/start"; // или другой путь, куда вы хотите перенаправить пользователя
  }
}
