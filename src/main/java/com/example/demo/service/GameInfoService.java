package com.example.demo.service;

import com.example.demo.domain.Type;
import com.example.demo.model.GameTypeBlock;
import com.example.demo.repository.TypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GameInfoService {

    private final TypeRepository typeRepository;

    public List<GameTypeBlock> getGameTypes() {
        return typeRepository.findAll().stream().map(this::map).toList();
    }

    private GameTypeBlock map(Type type) {
        return new GameTypeBlock(type.getId().toString(), type.getTypeName(), type.getDescription());
    }
}
