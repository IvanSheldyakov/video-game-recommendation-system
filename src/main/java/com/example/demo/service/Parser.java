package com.example.demo.service;

import com.example.demo.repository.GameRepository;
import com.example.demo.repository.TypeRepository;
import com.example.demo.repository.WordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
public class Parser {

    private final GameRepository gameRepository;


    private final TypeRepository typeRepository;


    private final WordRepository wordRepository;

    private final ExecutorService executor = Executors.newFixedThreadPool(8);

    public void parse() {
        try {
            for (int i = 0; i <= 198; i++) {
                PageParser parser = new PageParser(i, gameRepository, typeRepository, wordRepository);
                parser.start();
                parser.join();
                if (i == 9) {
                    System.out.println("done");
                    break;
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            executor.shutdown();
        }
    }
}
