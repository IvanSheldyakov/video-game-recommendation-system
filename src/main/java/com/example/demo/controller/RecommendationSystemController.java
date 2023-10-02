package com.example.demo.controller;

import com.example.demo.model.CustomBlock;
import com.example.demo.model.SearchCriteria;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping
public class RecommendationSystemController {


    @GetMapping("/start")
    public String showPlatform(Model model) {
        List<String> platforms = Arrays.asList("Вариант 1", "Вариант 2", "Вариант 3");
        List<String> genres = Arrays.asList("Жанр 1", "Жанр 2", "Жанр 3");
        List<String> publishers = Arrays.asList("Sony", "Your MOM");
        model.addAttribute("platforms", platforms);
        model.addAttribute("genres", genres);
        model.addAttribute("publishers", publishers);
        model.addAttribute("searchCriteria", new SearchCriteria());
        model.addAttribute("customBlocks", List.of(
                new CustomBlock("1","Название блока1", "Описание блока"),
                new CustomBlock("2","Название блока2", "Описание блока")));
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
