package com.raga.ecommerce.order.service;

import com.google.common.collect.ImmutableMap;
import com.raga.ecommerce.order.exception.AccountNotFoundException;
import com.raga.ecommerce.order.exception.ItemReservationFailedException;
import com.raga.ecommerce.order.exception.ShippingAddressNotFoundException;
import com.raga.ecommerce.order.repository.OrderRepository;
import com.raga.ecommerce.order.service.request.ReserveProductRequest;
import com.raga.ecommerce.order.service.response.AccountsResponse;
import com.raga.ecommerce.order.service.response.ReserveProductResponse;
import com.raga.ecommerce.order.vo.Address;
import com.raga.ecommerce.order.vo.Order;
import com.raga.ecommerce.order.web.response.Error;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static java.nio.charset.Charset.defaultCharset;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpHeaders.EMPTY;
import static org.springframework.http.HttpStatus.*;

@RunWith(MockitoJUnitRunner.class)
public class OrderServiceTest {

  private static final String ERRORS_FIELD = "errors";
  private static final String PLACEHOLDER_ERROR_MESSAGE = "Hello";

  @Mock
  private OrderRepository orderRepository;

  @Mock
  private RestTemplate restTemplate;

  @Mock
  private OrderIdGenerator orderIdGenerator;

  private OrderService orderService;

  @Before
  public void setUp() {
    orderService = new OrderService(orderRepository, restTemplate, orderIdGenerator);
  }

  @Test
  public void shouldReserveItems() {
    when(orderIdGenerator.generateOrderId()).thenReturn("order-123");

    Address address = new Address("address-123", "Flat 450, Marvel Zephyr",
      "near EON IT Park", "Pune", "Maharashtra", "India", "411014");
    AccountsResponse accountsResponse = new AccountsResponse("account-123",
      "John Wayne", newArrayList(address));
    when(restTemplate.getForEntity("/accounts/account-123", AccountsResponse.class))
      .thenReturn(ResponseEntity.ok(accountsResponse));

    ReserveProductRequest request = new ReserveProductRequest("order-123", 2, BigDecimal.TEN);
    ArrayList<String> items = newArrayList("item-a1", "item-a2");

    ReserveProductResponse reserveProductResponse = new ReserveProductResponse(items, BigDecimal.TEN);
    when(restTemplate.postForEntity(eq("/products/prod-123/reserve"), refEq(request), eq(ReserveProductResponse.class)))
      .thenReturn(ResponseEntity.ok(reserveProductResponse));

    String orderId = orderService.placeOrder("account-123", "prod-123", 2,
      BigDecimal.TEN, "address-123");

    ArgumentCaptor<Order> argumentCaptor = ArgumentCaptor.forClass(Order.class);
    verify(orderRepository, times(1)).save(argumentCaptor.capture());
    Order actual = argumentCaptor.getValue();

    assertThat(orderId).isEqualTo("order-123");
    assertThat(actual.getOrderId()).isEqualTo("order-123");
    assertThat(actual.getAccountId()).isEqualTo("account-123");
    assertThat(actual.getProductId()).isEqualTo("prod-123");
    assertThat(actual.getBillAmount()).isEqualTo(BigDecimal.valueOf(20));
    assertThat(actual.getOrderTimestamp()).isNotNull();
    assertThat(actual.getItems().get(0)).isEqualTo("item-a1");
    assertThat(actual.getItems().get(1)).isEqualTo("item-a2");
    assertThat(actual.getShippingAddress().getAddressId()).isEqualTo("address-123");
    assertThat(actual.getShippingAddress().getLineOne()).isEqualTo("Flat 450, Marvel Zephyr");
    assertThat(actual.getShippingAddress().getLineTwo()).isEqualTo("near EON IT Park");
    assertThat(actual.getShippingAddress().getCity()).isEqualTo("Pune");
    assertThat(actual.getShippingAddress().getState()).isEqualTo("Maharashtra");
    assertThat(actual.getShippingAddress().getCountry()).isEqualTo("India");
    assertThat(actual.getShippingAddress().getPinCode()).isEqualTo("411014");
  }

  @Test(expected = AccountNotFoundException.class)
  public void shouldThrowExceptionIfAccountNotFound() {
    when(restTemplate.getForEntity("/accounts/account-123", AccountsResponse.class))
      .thenThrow(HttpClientErrorException.create(NOT_FOUND, PLACEHOLDER_ERROR_MESSAGE,
        EMPTY, PLACEHOLDER_ERROR_MESSAGE.getBytes(), defaultCharset()));

    orderService.placeOrder("account-123", "prod-123", 2,
      BigDecimal.TEN, "address-123");
  }

  @Test(expected = HttpClientErrorException.class)
  public void shouldThrowHttpClientErrorExceptionItselfIfErrorStatusCodeIsOtherThanNotFoundDuringGetAccount() {
    when(restTemplate.getForEntity("/accounts/account-123", AccountsResponse.class))
      .thenThrow(HttpClientErrorException.create(INTERNAL_SERVER_ERROR, PLACEHOLDER_ERROR_MESSAGE,
        EMPTY, PLACEHOLDER_ERROR_MESSAGE.getBytes(), defaultCharset()));

    orderService.placeOrder("account-123", "prod-123", 2,
      BigDecimal.TEN, "address-123");
  }

  @Test(expected = ShippingAddressNotFoundException.class)
  public void shouldThrowShippingAddressNotFoundExceptionIfAddressDoesNotBelongToGivenAccount() {
    Address address = new Address("address-123", "Flat 450, Marvel Zephyr",
      "near EON IT Park", "Pune", "Maharashtra", "India", "411014");
    AccountsResponse accountsResponse = new AccountsResponse("accountId",
      "John Wayne", newArrayList(address));

    when(restTemplate.getForEntity("/accounts/account-123", AccountsResponse.class))
      .thenReturn(ResponseEntity.ok(accountsResponse));

    orderService.placeOrder("account-123", "prod-123", 2,
      BigDecimal.TEN, "address-456");
  }

  @Test(expected = ItemReservationFailedException.class)
  public void shouldThrowItemReservationFailedExceptionIfStatusCodeIsUnProcessableEntity() {
    when(orderIdGenerator.generateOrderId()).thenReturn("order-123");

    Address address = new Address("address-123", "Flat 450, Marvel Zephyr",
      "near EON IT Park", "Pune", "Maharashtra", "India", "411014");
    AccountsResponse accountsResponse = new AccountsResponse("account-123",
      "John Wayne", newArrayList(address));
    when(restTemplate.getForEntity("/accounts/account-123", AccountsResponse.class))
      .thenReturn(ResponseEntity.ok(accountsResponse));

    ReserveProductRequest request = new ReserveProductRequest("order-123", 2, BigDecimal.TEN);

    when(restTemplate.postForEntity(eq("/products/prod-123/reserve"), refEq(request), eq(ReserveProductResponse.class)))
      .thenThrow(HttpClientErrorException.create(UNPROCESSABLE_ENTITY, PLACEHOLDER_ERROR_MESSAGE,
        EMPTY, PLACEHOLDER_ERROR_MESSAGE.getBytes(), defaultCharset()));

    orderService.placeOrder("account-123", "prod-123", 2,
      BigDecimal.TEN, "address-123");
  }

  @Test(expected = HttpClientErrorException.class)
  public void shouldThrowHttpClientErrorExceptionItselfIfErrorStatusCodeIsOtherThanNotFoundDuringReservation() {
    when(orderIdGenerator.generateOrderId()).thenReturn("order-123");

    Address address = new Address("address-123", "Flat 450, Marvel Zephyr",
      "near EON IT Park", "Pune", "Maharashtra", "India", "411014");
    AccountsResponse accountsResponse = new AccountsResponse("account-123",
      "John Wayne", newArrayList(address));
    when(restTemplate.getForEntity("/accounts/account-123", AccountsResponse.class))
      .thenReturn(ResponseEntity.ok(accountsResponse));

    ReserveProductRequest request = new ReserveProductRequest("order-123", 2, BigDecimal.TEN);

    when(restTemplate.postForEntity(eq("/products/prod-123/reserve"), refEq(request), eq(ReserveProductResponse.class)))
      .thenThrow(HttpClientErrorException.create(INTERNAL_SERVER_ERROR, PLACEHOLDER_ERROR_MESSAGE,
        EMPTY, PLACEHOLDER_ERROR_MESSAGE.getBytes(), defaultCharset()));

    orderService.placeOrder("account-123", "prod-123", 2,
      BigDecimal.TEN, "address-123");
  }

  private Map<String, List<Error>> buildErrors(String code, String title, String message) {
    List<Error> errors = new ArrayList<>();

    Error error = new Error.ErrorBuilder()
      .withCode(code)
      .withTitle(title)
      .withMessage(message)
      .build();

    errors.add(error);

    return ImmutableMap.of(ERRORS_FIELD, errors);
  }
}