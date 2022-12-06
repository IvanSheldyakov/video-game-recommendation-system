package com.example.demo.controller;

import com.example.demo.domain.Game;
import com.example.demo.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ParserController {

    private final GameRepository gameRepository;

    @GetMapping("/start")
    public void start() {
        Game game = new Game();
        game.setName("my");
        game.setVector(List.of(2,2,5,6,7,8,3,7));
        gameRepository.save(game);
    }
}
