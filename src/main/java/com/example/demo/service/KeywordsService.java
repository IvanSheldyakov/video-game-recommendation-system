package com.example.demo.service;

import com.example.demo.domain.*;
import com.example.demo.repository.BlockWordRepository;
import com.example.demo.repository.TypeRepository;
import com.example.demo.repository.WordCountRepository;
import com.example.demo.repository.WordRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class KeywordsService {

  private final WordCountRepository wordCountRepository;
  private final TypeRepository typeRepository;
  private final WordRepository wordRepository;

  private final BlockWordRepository blockWordRepository;

  // public static double k = 1.5;

  private boolean needBlock = false;

  private final Comparator<WordCount> wordCountComparator =
      Comparator.comparing(WordCount::getCount);

  @Transactional
  public void update() {

    if (needBlock) {
      Set<BlockWord> wordCounts =
          wordCountRepository.findAll(Sort.by(Sort.Order.desc("count"))).stream()
              .limit(100)
              .map(
                  wordCount -> {
                    return new BlockWord(wordCount.getWord());
                  })
              .collect(Collectors.toSet());
      blockWordRepository.saveAll(wordCounts);
    }
    Set<String> blockWords =
        blockWordRepository.findAll().stream().map(BlockWord::getWord).collect(Collectors.toSet());
    List<Type> types = typeRepository.findAll();
    Set<String> allKeywords =
        types.stream()
            .flatMap(type -> wordRepository.findWordsByType(type).stream().map(Word::getWord))
            .collect(Collectors.toSet());

    List<Word> newKeyWords = new ArrayList<>();
    types.forEach(
        type -> {
          List<WordCount> countList =
              wordCountRepository.findAllByTypeNameOrderByCountAsc(type.getTypeName());
          double gameCount = type.getGames().size() * 0.7;
          if (countList.isEmpty()) return;

          List<WordCount> list =
              countList.stream().filter(wordCount -> wordCount.getInGames() > gameCount).toList();

          Bounds bounds = findBounds(list);
          //  System.out.println(bounds +
          // "---------------------------------------------------------------------------------------------------------------------");

          list.stream()
              .filter(wordCount -> bounds.inBounds(wordCount.getCount()))
              .map(WordCount::getWord)
              .filter(word -> !allKeywords.contains(word))
              .filter(word -> !blockWords.contains(word))
              .forEach(
                  newKeyword -> {
                    newKeyWords.add(new Word(newKeyword, type));
                    allKeywords.add(newKeyword);
                  });
        });
    // wordRepository.saveAll(newKeyWords);
    System.out.println(newKeyWords);
    System.out.println(newKeyWords.size());
  }

  private Double findMedian(List<WordCount> countList) {
    double median;
    int size = countList.size();
    if (size % 2 == 0) {
      // Если размер списка четный, возвращаем среднее значение двух средних элементов
      long leftMiddle = countList.get(size / 2 - 1).getCount();
      long rightMiddle = countList.get(size / 2).getCount();
      return median = (leftMiddle + rightMiddle) / 2.0;
    } else {
      // Если размер списка нечетный, возвращаем значение среднего элемента
      return median = Long.valueOf(countList.get(size / 2).getCount()).doubleValue();
    }
  }

  private Bounds findBounds(List<WordCount> countList) {
    Double Q1 = findQ1(countList);
    Double Q3 = findQ3(countList);
    Double IQR = Q3 - Q1;
    // Double lower = Q1 - (IQR * k);
    // Double upper = Q3 + (IQR * k);
    return new Bounds(Q1, Q3);
  }

  private Double findIQR(List<WordCount> countList) {
    return findQ3(countList) - findQ1(countList);
  }

  private Double findQ1(List<WordCount> countList) {
    double position = countList.size() * 0.25;
    return findQ(countList, position);
  }

  private Double findQ3(List<WordCount> countList) {
    double position = countList.size() * 0.75;
    return findQ(countList, position);
  }

  private Double findQ(List<WordCount> countList, double position) {
    if (position % 1 == 0) {
      // Если позиция является целым числом, возвращаем среднее значение двух элементов
      long left = countList.get((int) position - 1).getCount();
      long right = countList.get((int) position).getCount();
      return (left + right) / 2.0;
    } else {
      // Если позиция дробная, округляем ее вверх и возвращаем значение элемента на этой позиции
      return Long.valueOf(countList.get((int) Math.ceil(position) - 1).getCount()).doubleValue();
    }
  }
}
