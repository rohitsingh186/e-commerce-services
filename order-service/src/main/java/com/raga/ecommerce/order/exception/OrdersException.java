package com.raga.ecommerce.order.exception;

public abstract class OrdersException extends RuntimeException {

  public OrdersException(String message) {
    super(message);
  }

  public abstract String getTitle();
}
