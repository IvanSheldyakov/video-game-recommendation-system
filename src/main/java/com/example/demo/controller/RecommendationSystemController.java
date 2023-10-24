package com.example.demo.controller;

import com.example.demo.domain.Game;
import com.example.demo.mapper.GameMapper;
import com.example.demo.model.GameInfo;
import com.example.demo.model.GameTypeBlock;
import com.example.demo.model.SearchCriteria;
import com.example.demo.repository.GameRepository;
import com.example.demo.service.GameInfoService;
import com.example.demo.utils.Constants;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
  public String handleSubmit(SearchCriteria searchCriteria, RedirectAttributes redirectAttributes) {
    // Добавляем критерии поиска в атрибуты редиректа
    redirectAttributes.addFlashAttribute("searchCriteria", searchCriteria);
    return "redirect:/results"; // редирект на GET метод
  }

  @GetMapping("/results")
  public String handleResults(
      @RequestParam(defaultValue = "0") int page,
      @ModelAttribute("searchCriteria") SearchCriteria searchCriteria,
      Model model) {

    // Используем PageRequest для указания страницы и количества элементов на странице
    Pageable pageable = PageRequest.of(page, 10);

    // Используем метод findGameInfoByFilter для получения страницы игр
    Page<Game> gamePage = gameRepository.findGameInfoByFilter(searchCriteria, pageable);

    // Используем метод findDetailedGameInfoByFilter для получения детальной информации о играх
    List<GameInfo> games = gamePage.getContent().stream().map(GameMapper::toGameInfo).toList();

    model.addAttribute("games", games);
    model.addAttribute("currentPage", page);
    model.addAttribute("totalPages", gamePage.getTotalPages());

    return "game_info_page";
  }
}
