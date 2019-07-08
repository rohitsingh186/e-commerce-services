package com.raga.ecommerce.order.service.response;

import java.math.BigDecimal;
import java.util.List;

public class ReserveProductResponse {

  private List<String> items;
  private BigDecimal currentPrice;

  public ReserveProductResponse(List<String> items, BigDecimal currentPrice) {
    this.items = items;
    this.currentPrice = currentPrice;
  }

  public List<String> getItems() {
    return items;
  }

  public BigDecimal getCurrentPrice() {
    return currentPrice;
  }
}
