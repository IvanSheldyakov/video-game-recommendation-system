package com.example.demo.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "game")
@Getter
@Setter
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private Integer score;

    @Column(name = "trailer_link")
    private String trailerLink;

    @Column(name = "release_date", columnDefinition = "DATE")
    private LocalDate releaseDate;

    @Column(name = "summary", columnDefinition = "text")
    private String summary;

    @ElementCollection
    private List<Integer> vector;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "type_id")
    private Type type;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "rating_id")
    private Rating rating;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "publisher_id")
    private Publisher publisher;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "game_genre",
            joinColumns = @JoinColumn(name = "game_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id"))
    private Set<Genre> genres = new HashSet<>();

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "game_platform",
            joinColumns = @JoinColumn(name = "game_id"),
            inverseJoinColumns = @JoinColumn(name = "platform_id"))
    private Set<Platform> platforms = new HashSet<>();


    @Override
    public String toString() {
        return "Game{" +
                "name='" + name + '\'' +
                '}';
    }
}
