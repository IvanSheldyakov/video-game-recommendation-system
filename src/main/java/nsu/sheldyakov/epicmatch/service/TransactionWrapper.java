package nsu.sheldyakov.epicmatch.service;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TransactionWrapper {

  @Transactional
  public void wrapInTransaction(Runnable runnable) {
    runnable.run();
  }
}
