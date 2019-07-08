package com.raga.ecommerce.inventory.vo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document
public class ProductReservationLockLog {

  @Id
  private String productId;
  private String orderId;
  private LocalDateTime productLockedTimestamp;

  public ProductReservationLockLog(String productId, String orderId, LocalDateTime productLockedTimestamp) {
    this.productId = productId;
    this.orderId = orderId;
    this.productLockedTimestamp = productLockedTimestamp;
  }

  public String getProductId() {
    return productId;
  }

  public String getOrderId() {
    return orderId;
  }

  public LocalDateTime getProductLockedTimestamp() {
    return productLockedTimestamp;
  }
}
