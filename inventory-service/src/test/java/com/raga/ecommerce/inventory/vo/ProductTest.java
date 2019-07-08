package com.raga.ecommerce.inventory.vo;

import org.junit.Test;

import java.math.BigDecimal;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;

public class ProductTest {

  @Test
  public void shouldAddItemToTheInventory() {
    Product watch = new Product("prod-123", "Fastrack Watch", BigDecimal.valueOf(2222.25));

    assertThat(watch.getItems().size()).isEqualTo(0);

    watch.addItems(newArrayList("item-a1", "item-a2"));

    assertThat(watch.getItems().size()).isEqualTo(2);
  }

  @Test
  public void shouldReturnOnlyUnreservedItems() {
    Product watch = new Product("prod-123", "Fastrack Watch", BigDecimal.valueOf(2222.25));

    watch.addItems(newArrayList("item-a1", "item-a2"));

    watch.getItems().get(0).reserve("order-123");

    assertThat(watch.getUnreservedItems().size()).isEqualTo(1);
    assertThat(watch.getUnreservedItems().get(0).getItemId()).isEqualTo("item-a2");
  }
}