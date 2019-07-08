package com.raga.ecommerce.order.web.request;

import java.math.BigDecimal;

public class PlaceOrderRequest {
  private final String accountId;
  private final String productId;
  private final int quantity;
  private final BigDecimal expectedPricePerItem;
  private final String shippingAddressId;

  public PlaceOrderRequest(String accountId, String productId, int quantity,
                           BigDecimal expectedPricePerItem, String shippingAddressId) {

    this.accountId = accountId;
    this.productId = productId;
    this.quantity = quantity;
    this.expectedPricePerItem = expectedPricePerItem;
    this.shippingAddressId = shippingAddressId;
  }

  public String getAccountId() {
    return accountId;
  }

  public String getProductId() {
    return productId;
  }

  public int getQuantity() {
    return quantity;
  }

  public BigDecimal getExpectedPricePerItem() {
    return expectedPricePerItem;
  }

  public String getShippingAddressId() {
    return shippingAddressId;
  }
}
