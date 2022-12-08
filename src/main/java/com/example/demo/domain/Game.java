package com.example.demo.domain;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "game")
@Data
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String platform;

    private Integer score;

    private String publisher;


    @Column(columnDefinition = "DATE")
    private LocalDate releaseDate;

    @ElementCollection
    private List<Integer> vector;


}
