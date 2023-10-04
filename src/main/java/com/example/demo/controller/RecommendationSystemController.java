package com.example.demo.controller;

import com.example.demo.model.SearchCriteria;
import com.example.demo.service.GameInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping
@RequiredArgsConstructor
public class RecommendationSystemController {

    private final GameInfoService gameInfoService;

    @GetMapping("/start")
    public String showPlatform(Model model) {
        List<String> platforms = Arrays.asList("Вариант 1", "Вариант 2", "Вариант 3");
        List<String> genres = Arrays.asList("Жанр 1", "Жанр 2", "Жанр 3");
        List<String> publishers = Arrays.asList("Sony", "Your MOM");
        String[] colors = {
                "bg-pastel1", "bg-pastel2", "bg-pastel3", "bg-pastel4",
                "bg-pastel5", "bg-pastel6", "bg-pastel7", "bg-pastel8",
                "bg-pastel9", "bg-pastel10", "bg-pastel11", "bg-pastel12",
                "bg-pastel13", "bg-pastel14", "bg-pastel15", "bg-pastel16"
        };
        model.addAttribute("colors", colors);
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
