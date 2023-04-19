package com.example.demo.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "word_count")
@Getter
@Setter
public class WordCount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String word;

    private String typeName;

    private long count;

    @ElementCollection
    private Set<Integer> gameIds = new HashSet<>();

}
