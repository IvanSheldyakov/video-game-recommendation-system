package nsu.sheldyakov.epicmatch.repository;

import nsu.sheldyakov.epicmatch.domain.Platform;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlatformRepository extends JpaRepository<Platform, Integer> {

  Optional<Platform> findByPlatform(String platform);
}