package com.raga.ecommerce.inventory.vo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Document
public class Product {

  @Id
  private String productId;
  private String productName;
  private BigDecimal price;
  private List<Item> items;

  public Product(String productId, String productName, BigDecimal price) {
    this.productId = productId;
    this.productName = productName;
    this.price = price;
    this.items = new ArrayList<>();
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

  public List<Item> getItems() {
    return items;
  }

  public List<Item> getUnreservedItems() {
    return items.stream()
      .filter(Item::isNotReserved)
      .collect(Collectors.toList());
  }

  public void addItems(List<String> itemIds) {
    List<Item> itemsToBeAdded = itemIds.stream()
      .map(Item::new)
      .collect(Collectors.toList());

    this.items.addAll(itemsToBeAdded);
  }
}
