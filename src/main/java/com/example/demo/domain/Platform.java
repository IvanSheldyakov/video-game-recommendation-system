package com.example.demo.domain;

import java.util.Objects;
import java.util.Set;
import javax.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "platform")
@Getter
@Setter
@NoArgsConstructor
public class Platform {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String platform;

  @ManyToMany private Set<Game> games;

  public Platform(String platform) {
    this.platform = platform;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Platform)) return false;
    Platform platform1 = (Platform) o;
    return Objects.equals(getPlatform(), platform1.getPlatform());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getPlatform());
  }
}
