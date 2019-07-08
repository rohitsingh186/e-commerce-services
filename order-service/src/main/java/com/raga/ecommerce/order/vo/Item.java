package com.raga.ecommerce.order.vo;

public class Item {

  private String itemId;
  private String itemName;

  public Item(String itemId, String itemName) {
    this.itemId = itemId;
    this.itemName = itemName;
  }

  public String getItemId() {
    return itemId;
  }

  public String getItemName() {
    return itemName;
  }
}
