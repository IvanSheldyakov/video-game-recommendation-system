package com.example.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Bounds {

  private Double lower;
  private Double upper;

  public boolean inBounds(Long value) {
    return lower < value && upper > value;
  }

  @Override
  public String toString() {
    return "Bounds{" + "lower=" + lower + ", upper=" + upper + '}';
  }
}
