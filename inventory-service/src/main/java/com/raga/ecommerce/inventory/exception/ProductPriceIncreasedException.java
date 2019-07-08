package com.raga.ecommerce.inventory.exception;

import java.math.BigDecimal;

public class ProductPriceIncreasedException extends ProductsException {

  private static final String MESSAGE = "Product price is more than expected for product with id: %s. " +
    "Expected: %s, Current: %s";
  private static final String TITLE = "Product Price More Than Expected";

  public ProductPriceIncreasedException(String productId, BigDecimal expected, BigDecimal current) {
    super(String.format(MESSAGE, productId, expected, current));
  }

  @Override
  public String getTitle() {
    return TITLE;
  }
}
