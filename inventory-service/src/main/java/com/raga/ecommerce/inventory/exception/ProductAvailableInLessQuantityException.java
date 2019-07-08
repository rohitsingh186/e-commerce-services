package com.raga.ecommerce.inventory.exception;

public class ProductAvailableInLessQuantityException extends ProductsException {

  private static final String MESSAGE = "%d quantity of product with id %s is not available. Available quantity is: %d";
  private static final String TITLE = "Product Available In Less Quantity";

  public ProductAvailableInLessQuantityException(String productId, int requiredQuantity, int availableQuantity) {
    super(String.format(MESSAGE, requiredQuantity, productId, availableQuantity));
  }

  @Override
  public String getTitle() {
    return TITLE;
  }
}
