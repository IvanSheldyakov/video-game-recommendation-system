package com.example.demo.domain;

import java.util.List;
import javax.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "publisher")
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Publisher {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String publisher;

  @OneToMany(mappedBy = "publisher")
  private List<Game> games;

  public Publisher(String publisher) {
    this.publisher = publisher;
  }
}
