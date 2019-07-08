package com.raga.ecommerce.inventory.vo;

import java.util.Objects;

public class Item {

  private String itemId;
  private String reservedByOrderId;

  public Item(String itemId) {
    this.itemId = itemId;
  }

  public String getItemId() {
    return itemId;
  }

  public boolean reserve(String orderId) {
    if (Objects.isNull(reservedByOrderId)) {
      reservedByOrderId = orderId;
      return true;
    } else {
      return false;
    }
  }

  public boolean isNotReserved() {
    return Objects.isNull(reservedByOrderId);
  }
}
