package com.raga.ecommerce.inventory.service;

import com.raga.ecommerce.inventory.exception.ProductAvailableInLessQuantityException;
import com.raga.ecommerce.inventory.exception.ProductPriceIncreasedException;
import com.raga.ecommerce.inventory.exception.ProductUnavailableException;
import com.raga.ecommerce.inventory.lock.ProductReservationLockManager;
import com.raga.ecommerce.inventory.repository.ProductRepository;
import com.raga.ecommerce.inventory.vo.Product;
import com.raga.ecommerce.inventory.web.response.ReserveProductResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProductServiceTest {

  @Mock
  private ProductRepository productRepository;

  @Mock
  private ProductReservationLockManager productReservationLockManager;

  private ProductService productService;

  @Before
  public void setUp() {
    productService = new ProductService(productRepository, productReservationLockManager);
    when(productReservationLockManager.getLock(anyString())).thenReturn(new ReentrantLock());
  }

  @Test
  public void shouldReturnOnlyAvailableProducts() {
    Product watch = new Product("prod-123", "Fasttrack Watch", BigDecimal.valueOf(2222.25));
    List<String> items = newArrayList("item-a1", "item-a2");
    watch.addItems(items);
    Product laptop = new Product("prod-456", "Lenovo Thinkpad", BigDecimal.valueOf(72222.59));
    List<Product> products = newArrayList(watch, laptop);
    when(productRepository.findAll()).thenReturn(products);

    List<Product> actual = productService.getAvailableProducts();
    assertThat(actual.size()).isEqualTo(1);
    assertThat(actual.get(0).getProductId()).isEqualTo("prod-123");
  }

  @Test(expected = ProductUnavailableException.class)
  public void shouldThrowExceptionIfProductNotAvailable() {
    when(productRepository.findByProductId("prod-456")).thenReturn(Optional.empty());

    productService.reserveItems("order-123", "prod-456", 2, BigDecimal.valueOf(2222.25));
  }

  @Test(expected = ProductUnavailableException.class)
  public void shouldThrowExceptionIfNoItemOfTheProductIsAvailable() {
    Product watch = new Product("prod-456", "Fasttrack Watch", BigDecimal.valueOf(2222.25));
    when(productRepository.findByProductId("prod-456")).thenReturn(Optional.of(watch));

    productService.reserveItems(
      "order-123", "prod-456", 2, BigDecimal.valueOf(2222.25));
  }

  @Test(expected = ProductAvailableInLessQuantityException.class)
  public void shouldThrowExceptionIfNotEnoughQuantityOfItemsOfTheProductIsAvailable() {
    Product watch = new Product("prod-456", "Fasttrack Watch", BigDecimal.valueOf(2222.25));
    List<String> items = newArrayList("item-a1", "item-a2");
    watch.addItems(items);
    when(productRepository.findByProductId("prod-456")).thenReturn(Optional.of(watch));

    productService.reserveItems(
      "order-123", "prod-456", 3, BigDecimal.valueOf(2222.25));
  }

  @Test(expected = ProductPriceIncreasedException.class)
  public void shouldThrowExceptionIfCurrentPriceIsMoreThanExpectedPrice() {
    Product watch = new Product("prod-456", "Fasttrack Watch", BigDecimal.valueOf(2222.25));
    List<String> items = newArrayList("item-a1", "item-a2");
    watch.addItems(items);
    when(productRepository.findByProductId("prod-456")).thenReturn(Optional.of(watch));

    productService.reserveItems(
      "order-123", "prod-456", 2, BigDecimal.valueOf(2000.00));
  }

  @Test
  public void shouldReserveItemsIfAvailableAtExpectedPrice() {
    Product watch = new Product("prod-456", "Fasttrack Watch", BigDecimal.valueOf(2222.25));
    List<String> items = newArrayList("item-a1", "item-a2");
    watch.addItems(items);
    when(productRepository.findByProductId("prod-456")).thenReturn(Optional.of(watch));

    ReserveProductResponse response = productService.reserveItems(
      "order-123", "prod-456", 2, BigDecimal.valueOf(2222.25));
    assertThat(response.getItems()).isEqualTo(items);
    assertThat(response.getCurrentPrice()).isEqualTo(BigDecimal.valueOf(2222.25));
  }

  @Test
  public void shouldReserveItemsIfAvailableAtLessThanExpectedPrice() {
    Product watch = new Product("prod-456", "Fasttrack Watch", BigDecimal.valueOf(2222.25));
    List<String> items = newArrayList("item-a1", "item-a2");
    watch.addItems(items);
    when(productRepository.findByProductId("prod-456")).thenReturn(Optional.of(watch));

    ReserveProductResponse response = productService.reserveItems(
      "order-123", "prod-456", 2, BigDecimal.valueOf(2400.00));
    assertThat(response.getItems()).isEqualTo(items);
    assertThat(response.getCurrentPrice()).isEqualTo(BigDecimal.valueOf(2222.25));
  }

  @Test
  public void shouldMarkItemAsReserved() {
    Product watch = new Product("prod-456", "Fasttrack Watch", BigDecimal.valueOf(2222.25));
    List<String> items = newArrayList("item-a1");
    watch.addItems(items);
    when(productRepository.findByProductId("prod-456")).thenReturn(Optional.of(watch));

    productService.reserveItems("order-123", "prod-456", 1, BigDecimal.valueOf(2222.25));

    Product expectedProduct = new Product("prod-456", "Fasttrack Watch",
      BigDecimal.valueOf(2222.25));
    expectedProduct.addItems(items);
    expectedProduct.getItems().get(0).reserve("order-123");

    verify(productRepository, times(1)).save(any());
  }
}