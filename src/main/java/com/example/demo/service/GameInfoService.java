package com.example.demo.service;

import com.example.demo.domain.Genre;
import com.example.demo.domain.Platform;
import com.example.demo.domain.Publisher;
import com.example.demo.domain.Type;
import com.example.demo.model.GameTypeBlock;
import com.example.demo.repository.GenreRepository;
import com.example.demo.repository.PlatformRepository;
import com.example.demo.repository.PublisherRepository;
import com.example.demo.repository.TypeRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameInfoService {

  private final TypeRepository typeRepository;
  private final PublisherRepository publisherRepository;
  private final GenreRepository genreRepository;
  private final PlatformRepository platformRepository;

  public List<GameTypeBlock> getGameTypes() {
    return typeRepository.findAll().stream().map(this::map).toList();
  }

  public List<String> getPublishers() {
    return publisherRepository.findAll().stream().map(Publisher::getPublisher).toList();
  }

  public List<String> getGenres() {
    return genreRepository.findAll().stream().map(Genre::getGenre).toList();
  }

  public List<String> getPlatforms() {
    return platformRepository.findAll().stream().map(Platform::getPlatform).toList();
  }

  private GameTypeBlock map(Type type) {
    return new GameTypeBlock(type.getId().toString(), type.getTypeName(), type.getDescription());
  }
}
