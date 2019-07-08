package com.raga.ecommerce.order.service.request;

import java.math.BigDecimal;

public class ReserveProductRequest {

  private String orderId;
  private int quantity;
  private BigDecimal expectedPrice;

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
