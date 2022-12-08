package com.example.demo.controller;

import com.example.demo.service.Parser;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ParserController {

    private final Parser parser;

    @GetMapping("/start")
    public void start() {
        parser.parse();
    }
}
