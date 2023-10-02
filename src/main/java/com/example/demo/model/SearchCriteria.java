package com.example.demo.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class SearchCriteria {
    private String platform;
    private Integer minScore;
    private String genre;
    private String publisher;
    private LocalDate releaseStartDate;
    private LocalDate releaseEndDate;
}