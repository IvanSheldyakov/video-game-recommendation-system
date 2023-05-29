package com.example.demo.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "word_count")
@Getter
@Setter
@NoArgsConstructor
public class WordCount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String word;

    private String typeName;

    private long count;

    private int inGames;

    public WordCount(String word, String typeName) {
        this.word = word;
        this.typeName = typeName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WordCount)) return false;
        WordCount wordCount = (WordCount) o;
        return Objects.equals(getWord(), wordCount.getWord()) && Objects.equals(getTypeName(), wordCount.getTypeName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getWord(), getTypeName());
    }
}
