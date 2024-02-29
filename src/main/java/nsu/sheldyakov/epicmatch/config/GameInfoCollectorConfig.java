package nsu.sheldyakov.epicmatch.config;

import javax.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import nsu.sheldyakov.epicmatch.service.WordsFinder;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
@ComponentScan("nsu.sheldyakov.epicmatch")
@RequiredArgsConstructor
public class GameInfoCollectorConfig {

  @Bean
  public WebDriver webDriver() {
    System.setProperty("webdriver.chrome.driver", "C:\\Users\\Public\\chromedriver.exe");
    ChromeOptions options = new ChromeOptions();
    options.addArguments("--remote-allow-origins=*");
    return new ChromeDriver(options);
  }

  @Bean
  @Scope(scopeName = "prototype")
  public WordsFinder wordsFinder() {
    return new WordsFinder();
  }

  @PreDestroy
  public void clean() {
    webDriver().quit();
  }
}
