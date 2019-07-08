package com.raga.ecommerce.order.service;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrderIdGenerator {

  public String generateOrderId() {
    return UUID.randomUUID().toString();
  }
}
