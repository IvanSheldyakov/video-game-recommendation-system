package nsu.sheldyakov.epicmatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableRetry
@EnableScheduling
@EnableAsync
public class EpicmatchApplication {

  public static void main(String[] args) {
    SpringApplication.run(EpicmatchApplication.class, args);
  }
}
