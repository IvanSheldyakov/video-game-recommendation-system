package nsu.sheldyakov.epicmatch.service;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TransactionWrapper {

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @Modifying(flushAutomatically = true, clearAutomatically = true)
  public void wrapInTransaction(Runnable runnable) {
    runnable.run();
  }
}
