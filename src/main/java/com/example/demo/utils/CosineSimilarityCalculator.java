package com.example.demo.utils;

import java.util.List;

public class CosineSimilarityCalculator {

  /**
   * Calculate cosine similarity between two normalized vectors.
   *
   * @param vectorA First normalized vector.
   * @param vectorB Second normalized vector.
   * @return Cosine similarity value between -1 and 1.
   */
  public static double calculateCosineSimilarity(List<Double> vectorA, List<Double> vectorB) {
    if (vectorA.size() != vectorB.size()) {
      throw new IllegalArgumentException("Vectors must be of the same length");
    }

    double dotProduct = 0.0;

    for (int i = 0; i < vectorA.size(); i++) {
      dotProduct += vectorA.get(i) * vectorB.get(i);
    }

    return dotProduct;
  }
}
