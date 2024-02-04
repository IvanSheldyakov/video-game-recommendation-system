package nsu.sheldyakov.epicmatch.repository;

import java.util.Optional;
import nsu.sheldyakov.epicmatch.domain.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublisherRepository extends JpaRepository<Publisher, Integer> {

  Optional<Publisher> findByPublisher(String publisher);
}
