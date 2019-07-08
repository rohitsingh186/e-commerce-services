package com.raga.ecommerce.inventory.exception;

public class ProductUnavailableException extends ProductsException {

  private static final String MESSAGE = "Product not available with id: %s";
  private static final String TITLE = "Product Not Available";

  public ProductUnavailableException(String productId) {
    super(String.format(MESSAGE, productId));
  }

  @Override
  public String getTitle() {
    return TITLE;
  }
}
