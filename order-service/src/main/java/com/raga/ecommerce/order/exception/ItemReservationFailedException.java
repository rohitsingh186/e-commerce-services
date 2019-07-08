package com.raga.ecommerce.order.exception;

public class ItemReservationFailedException extends OrdersException {

  private static final String TITLE = "Unable to place order";

  public ItemReservationFailedException(String message) {
    super(message);
  }

  @Override
  public String getTitle() {
    return TITLE;
  }
}
