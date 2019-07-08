package com.raga.ecommerce.inventory.lock;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class ProductReservationLockManager {

  private final Map<String, Lock> locks;

  private ProductReservationLockManager() {
    locks = new HashMap<>();
  }

  public Lock getLock(String productId) {
    if (!locks.containsKey(productId)) {
      Lock lock = new ReentrantLock();
      locks.put(productId, lock);
    }

    return locks.get(productId);
  }
}
