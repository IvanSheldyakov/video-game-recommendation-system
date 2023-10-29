package com.example.demo.service;

import com.example.demo.domain.*;
import com.example.demo.model.GameTypeBlock;
import com.example.demo.repository.*;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameInfoService {

  private final TypeRepository typeRepository;
  private final PublisherRepository publisherRepository;
  private final GenreRepository genreRepository;
  private final PlatformRepository platformRepository;
  private final RatingRepository ratingRepository;
  private final GameRepository gameRepository;

  public List<GameTypeBlock> getGameTypes() {
    return typeRepository.findAll().stream().map(this::map).toList();
  }

  public List<String> getPublishers() {
    return publisherRepository.findAll(Sort.by(Sort.Order.asc("publisher"))).stream()
        .map(Publisher::getPublisher)
        .toList();
  }

  public List<String> getGenres() {
    return genreRepository.findAll(Sort.by(Sort.Order.asc("genre"))).stream()
        .map(Genre::getGenre)
        .toList();
  }

  public List<String> getPlatforms() {
    return platformRepository.findAll(Sort.by(Sort.Order.asc("platform"))).stream()
        .map(Platform::getPlatform)
        .toList();
  }

  public List<String> getRatings() {
    return ratingRepository.findAll(Sort.by(Sort.Order.asc("rating"))).stream()
        .map(Rating::getRating)
        .toList();
  }

  private GameTypeBlock map(Type type) {
    return new GameTypeBlock(type.getId().toString(), type.getTypeName(), type.getDescription());
  }
}
