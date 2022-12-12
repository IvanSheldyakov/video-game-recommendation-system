package com.example.demo.domain;

import lombok.Data;

import javax.persistence.*;

import java.util.List;


@Entity
@Table(name = "type")
@Data
public class Type {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String typeName;

    @OneToMany(mappedBy = "type")
    private List<Word> words;

}
