package com.raga.ecommerce.order.exception;

public class ShippingAddressNotFoundException extends OrdersException {

  private static final String MESSAGE = "Shipping address not found with addressId: %s and accountId: %s";
  private static final String TITLE = "Shipping address not found";

  public ShippingAddressNotFoundException(String accountId, String addressId) {
    super(String.format(MESSAGE, addressId, accountId));
  }

  @Override
  public String getTitle() {
    return TITLE;
  }
}
