package com.raga.ecommerce.order.vo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Document
public class Order {

  @Id
  private String orderId;
  private String accountId;
  private String productId;
  private List<String> items;
  private BigDecimal billAmount;
  private LocalDateTime orderTimestamp;
  private Address shippingAddress;

  public Order(String orderId, String accountId, String productId, List<String> items,
               BigDecimal billAmount, LocalDateTime orderTimestamp, Address shippingAddress) {
    this.orderId = orderId;
    this.accountId = accountId;
    this.productId = productId;
    this.items = items;
    this.billAmount = billAmount;
    this.orderTimestamp = orderTimestamp;
    this.shippingAddress = shippingAddress;
  }

  public String getOrderId() {
    return orderId;
  }

  public String getAccountId() {
    return accountId;
  }

  public String getProductId() {
    return productId;
  }

  public List<String> getItems() {
    return items;
  }

  public BigDecimal getBillAmount() {
    return billAmount;
  }

  public LocalDateTime getOrderTimestamp() {
    return orderTimestamp;
  }

  public Address getShippingAddress() {
    return shippingAddress;
  }
}
