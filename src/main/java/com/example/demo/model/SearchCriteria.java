package com.example.demo.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class SearchCriteria {
    private String platform;
    private Integer minScore;
    private String genre;
    private String publisher;
    private LocalDate releaseStartDate;
    private LocalDate releaseEndDate;
    private List<String> customValues;
}