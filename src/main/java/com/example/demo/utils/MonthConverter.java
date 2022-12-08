package com.example.demo.utils;

public class MonthConverter {
    public static int convert(String month) {
        return switch (month) {
            case "Jan" -> 0;
            case "Feb" -> 1;
            case "Mar" -> 2;
            case "Apr" -> 3;
            case "May" -> 4;
            case "Jun" -> 5;
            case "Jul" -> 6;
            case "Aug" -> 7;
            case "Sep" -> 8;
            case "Oct" -> 9;
            case "Nov" -> 10;
            case "Dec" -> 11;
            default -> throw new RuntimeException();
        };
    }
}
