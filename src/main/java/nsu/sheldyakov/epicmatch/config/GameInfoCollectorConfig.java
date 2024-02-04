package nsu.sheldyakov.epicmatch.config;

import javax.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("nsu.sheldyakov.epicmatch")
@RequiredArgsConstructor
public class GameInfoCollectorConfig {

  @Bean
  public WebDriver webDriver() {
    System.setProperty("webdriver.chrome.driver", "C:\\Users\\Public\\chromedriver.exe");
    ChromeOptions options = new ChromeOptions();
    options.addArguments("--remote-allow-origins=*");
    WebDriver webDriver = new ChromeDriver(options);
    // webDriver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    return webDriver;
  }

  @PreDestroy
  public void clean() {
    webDriver().quit();
  }
}
