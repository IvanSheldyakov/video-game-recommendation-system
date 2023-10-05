package com.example.demo.repository;

import com.example.demo.domain.Publisher;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublisherRepository extends JpaRepository<Publisher, Integer> {

  Optional<Publisher> findByPublisher(String publisher);
}
