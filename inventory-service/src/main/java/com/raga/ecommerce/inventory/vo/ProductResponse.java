package com.raga.ecommerce.inventory.vo;

import java.math.BigDecimal;

public class ProductResponse {

  private String productId;
  private String productName;
  private BigDecimal price;

  public ProductResponse(Product product) {
    this.productId = product.getProductId();
    this.productName = product.getProductName();
    this.price = product.getPrice();
  }

  public String getProductId() {
    return productId;
  }

  public String getProductName() {
    return productName;
  }

  public BigDecimal getPrice() {
    return price;
  }
}
