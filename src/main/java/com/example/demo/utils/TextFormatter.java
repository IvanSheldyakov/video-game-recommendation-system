package com.example.demo.utils;

import java.util.Objects;
import java.util.regex.Pattern;

public class TextFormatter {

  public static String removeTextInBracketsAfterDot(String input) {
    if (Objects.isNull(input)) {
      return null;
    }
    String pattern = "\\. \\[.*?\\]";
    Pattern r = Pattern.compile(pattern);

    // Заменяем все найденные соответствия на точку
    return r.matcher(input).replaceAll(".");
  }
}
