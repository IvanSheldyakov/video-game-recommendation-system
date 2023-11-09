package com.example.demo.utils;

import java.util.List;

public class VectorNormalizer {

  public static Double[] normalize(List<Integer> vector) {
    double norm = calculateNorm(vector);

    Double[] normalizedVector = new Double[vector.size()];
    int index = 0;
    for (Integer value : vector) {
      if (norm == 0) {
        normalizedVector[index] = 0.0;
      } else {
        normalizedVector[index] = value / norm;
      }
      index++;
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
