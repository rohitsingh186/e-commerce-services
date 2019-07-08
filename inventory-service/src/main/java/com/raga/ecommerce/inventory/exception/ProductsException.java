package com.raga.ecommerce.inventory.exception;

public abstract class ProductsException extends RuntimeException {

  public ProductsException(String message) {
    super(message);
  }

  public abstract String getTitle();
}
