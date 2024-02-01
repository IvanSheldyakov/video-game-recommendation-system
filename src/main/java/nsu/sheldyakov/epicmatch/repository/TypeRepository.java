package nsu.sheldyakov.epicmatch.repository;

import nsu.sheldyakov.epicmatch.domain.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TypeRepository extends JpaRepository<Type, Integer> {}
