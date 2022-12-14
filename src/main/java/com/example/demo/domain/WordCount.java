package com.example.demo.domain;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "word_count")
@Data
public class WordCount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String word;

    private String typeName;

    private long count;

}
