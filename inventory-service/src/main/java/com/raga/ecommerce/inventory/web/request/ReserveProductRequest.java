package com.raga.ecommerce.inventory.web.request;

import java.math.BigDecimal;

public class ReserveProductRequest {

  private final String orderId;
  private final int quantity;
  private final BigDecimal expectedPrice;

  public ReserveProductRequest(String orderId, int quantity, BigDecimal expectedPrice) {
    this.orderId = orderId;
    this.quantity = quantity;
    this.expectedPrice = expectedPrice;
  }

  public String getOrderId() {
    return orderId;
  }

  public int getQuantity() {
    return quantity;
  }

  public BigDecimal getExpectedPrice() {
    return expectedPrice;
  }
}
