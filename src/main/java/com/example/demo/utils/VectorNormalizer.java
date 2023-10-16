package com.example.demo.utils;

import java.util.ArrayList;
import java.util.List;

public class VectorNormalizer {

  public static List<Double> normalize(List<Integer> vector) {
    double norm = calculateNorm(vector);

    List<Double> normalizedVector = new ArrayList<>();
    for (Integer value : vector) {
      normalizedVector.add(value / norm);
    }
    return normalizedVector;
  }

  private static double calculateNorm(List<Integer> vector) {
    double sum = 0;
    for (Integer value : vector) {
      sum += value * value;
    }
    return Math.sqrt(sum);
  }
}
