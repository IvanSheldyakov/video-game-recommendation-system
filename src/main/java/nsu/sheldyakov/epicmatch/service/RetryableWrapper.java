package nsu.sheldyakov.epicmatch.service;

import java.io.IOException;
import java.util.concurrent.Callable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
public class RetryableWrapper {

  @Retryable(value = IOException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
  public <T> T wrapInRetryable(Callable<T> callable) throws Exception {
    return callable.call();
  }
}
