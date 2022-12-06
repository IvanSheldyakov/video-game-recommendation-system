package com.example.demo.domain;

import lombok.Data;

import javax.persistence.*;
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

    private String realiseYear;

    @ElementCollection
    private List<Integer> vector;


}
