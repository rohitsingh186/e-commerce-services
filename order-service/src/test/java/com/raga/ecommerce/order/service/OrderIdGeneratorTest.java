package com.raga.ecommerce.order.service;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderIdGeneratorTest {

  @Test
  public void shouldGenerateOrderId() {
    OrderIdGenerator orderIdGenerator = new OrderIdGenerator();

    assertThat(orderIdGenerator.generateOrderId()).isNotBlank();
  }
}