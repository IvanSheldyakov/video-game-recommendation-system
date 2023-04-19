package com.example.demo.service;

import com.example.demo.domain.Bounds;
import com.example.demo.domain.Type;
import com.example.demo.domain.Word;
import com.example.demo.domain.WordCount;
import com.example.demo.repository.TypeRepository;
import com.example.demo.repository.WordCountRepository;
import com.example.demo.repository.WordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KeywordsService {

    private final WordCountRepository wordCountRepository;
    private final TypeRepository typeRepository;
    private final WordRepository wordRepository;

    public static double k = 0;

    @Transactional
    public void update() {
        List<Type> types = typeRepository.findAll();
        List<String> allKeywords = types.stream()
                .flatMap(type ->
                        wordRepository.findWordsByType(type).stream()
                                .map(Word::getWord)).collect(Collectors.toList());

        List<Word> newKeyWords = new ArrayList<>();
        types.forEach(
                type -> {
                    List<WordCount> countList = wordCountRepository.findAllByTypeNameOrderByCountAsc(type.getTypeName());
                    long gameCount = type.getGames().stream().count();
                    if (countList.isEmpty()) return;
                   // Bounds bounds = findBounds(countList);
                    //System.out.println(bounds + "---------------------------------------------------------------------------------------------------------------------");
                    countList.stream()
                            .filter(wordCount -> wordCount.getGameIds().size() == gameCount)
                            .map(WordCount::getWord)
                            .filter(word -> !allKeywords.contains(word))
                            .forEach(newKeyword -> {
                                newKeyWords.add(new Word(newKeyword, type));
                                allKeywords.add(newKeyword);
                            });
                }
        );
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
        Double lower = Q1 - (IQR * k);
        Double upper = Q3 + (IQR * k);
        return new Bounds(lower, upper);

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
