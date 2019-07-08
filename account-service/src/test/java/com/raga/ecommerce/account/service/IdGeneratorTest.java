package com.raga.ecommerce.account.service;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class IdGeneratorTest {

  @Test
  public void shouldGenerateOrderId() {
    IdGenerator orderIdGenerator = new IdGenerator();

    assertThat(orderIdGenerator.generateId()).isNotBlank();
  }
}