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
  private static final String ORDER_ID = "order-123";
  private static final String ADDRESS_ID = "address-123";
  private static final String LINE_ONE = "Flat 450, Marvel Zephyr";
  private static final String LINE_TWO = "near EON IT Park";
  private static final String CITY = "Pune";
  private static final String STATE = "Maharashtra";
  private static final String COUNTRY = "India";
  private static final String PIN_CODE = "411014";
  private static final String ACCOUNT_ID = "account-123";
  private static final String NAME = "John Wayne";
  private static final String PRODUCT_ID = "prod-123";

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
    when(orderIdGenerator.generateOrderId()).thenReturn(ORDER_ID);

    Address address = new Address(ADDRESS_ID, LINE_ONE,
      LINE_TWO, CITY, STATE, COUNTRY, PIN_CODE);
    AccountsResponse accountsResponse = new AccountsResponse(ACCOUNT_ID,
      NAME, newArrayList(address));
    when(restTemplate.getForEntity("/accounts/account-123", AccountsResponse.class))
      .thenReturn(ResponseEntity.ok(accountsResponse));

    ReserveProductRequest request = new ReserveProductRequest(ORDER_ID, 2, BigDecimal.TEN);
    ArrayList<String> items = newArrayList("item-a1", "item-a2");

    ReserveProductResponse reserveProductResponse = new ReserveProductResponse(items, BigDecimal.TEN);
    when(restTemplate.postForEntity(eq("/products/prod-123/reserve"), refEq(request), eq(ReserveProductResponse.class)))
      .thenReturn(ResponseEntity.ok(reserveProductResponse));

    String orderId = orderService.placeOrder(ACCOUNT_ID, PRODUCT_ID, 2,
      BigDecimal.TEN, ADDRESS_ID);

    ArgumentCaptor<Order> argumentCaptor = ArgumentCaptor.forClass(Order.class);
    verify(orderRepository, times(1)).save(argumentCaptor.capture());
    Order actual = argumentCaptor.getValue();

    assertThat(orderId).isEqualTo(ORDER_ID);
    assertThat(actual.getOrderId()).isEqualTo(ORDER_ID);
    assertThat(actual.getAccountId()).isEqualTo(ACCOUNT_ID);
    assertThat(actual.getProductId()).isEqualTo(PRODUCT_ID);
    assertThat(actual.getBillAmount()).isEqualTo(BigDecimal.valueOf(20));
    assertThat(actual.getOrderTimestamp()).isNotNull();
    assertThat(actual.getItems().get(0)).isEqualTo("item-a1");
    assertThat(actual.getItems().get(1)).isEqualTo("item-a2");
    assertThat(actual.getShippingAddress().getAddressId()).isEqualTo(ADDRESS_ID);
    assertThat(actual.getShippingAddress().getLineOne()).isEqualTo(LINE_ONE);
    assertThat(actual.getShippingAddress().getLineTwo()).isEqualTo(LINE_TWO);
    assertThat(actual.getShippingAddress().getCity()).isEqualTo(CITY);
    assertThat(actual.getShippingAddress().getState()).isEqualTo(STATE);
    assertThat(actual.getShippingAddress().getCountry()).isEqualTo(COUNTRY);
    assertThat(actual.getShippingAddress().getPinCode()).isEqualTo(PIN_CODE);
  }

  @Test(expected = AccountNotFoundException.class)
  public void shouldThrowExceptionIfAccountNotFound() {
    when(restTemplate.getForEntity("/accounts/account-123", AccountsResponse.class))
      .thenThrow(HttpClientErrorException.create(NOT_FOUND, PLACEHOLDER_ERROR_MESSAGE,
        EMPTY, PLACEHOLDER_ERROR_MESSAGE.getBytes(), defaultCharset()));

    orderService.placeOrder(ACCOUNT_ID, PRODUCT_ID, 2,
      BigDecimal.TEN, ADDRESS_ID);
  }

  @Test(expected = HttpClientErrorException.class)
  public void shouldThrowHttpClientErrorExceptionItselfIfErrorStatusCodeIsOtherThanNotFoundDuringGetAccount() {
    when(restTemplate.getForEntity("/accounts/account-123", AccountsResponse.class))
      .thenThrow(HttpClientErrorException.create(INTERNAL_SERVER_ERROR, PLACEHOLDER_ERROR_MESSAGE,
        EMPTY, PLACEHOLDER_ERROR_MESSAGE.getBytes(), defaultCharset()));

    orderService.placeOrder(ACCOUNT_ID, PRODUCT_ID, 2,
      BigDecimal.TEN, ADDRESS_ID);
  }

  @Test(expected = ShippingAddressNotFoundException.class)
  public void shouldThrowShippingAddressNotFoundExceptionIfAddressDoesNotBelongToGivenAccount() {
    Address address = new Address(ADDRESS_ID, LINE_ONE,
      LINE_TWO, CITY, STATE, COUNTRY, PIN_CODE);
    AccountsResponse accountsResponse = new AccountsResponse("accountId",
      NAME, newArrayList(address));

    when(restTemplate.getForEntity("/accounts/account-123", AccountsResponse.class))
      .thenReturn(ResponseEntity.ok(accountsResponse));

    orderService.placeOrder(ACCOUNT_ID, PRODUCT_ID, 2,
      BigDecimal.TEN, "address-456");
  }

  @Test(expected = ItemReservationFailedException.class)
  public void shouldThrowItemReservationFailedExceptionIfStatusCodeIsUnProcessableEntity() {
    when(orderIdGenerator.generateOrderId()).thenReturn(ORDER_ID);

    Address address = new Address(ADDRESS_ID, LINE_ONE,
      LINE_TWO, CITY, STATE, COUNTRY, PIN_CODE);
    AccountsResponse accountsResponse = new AccountsResponse(ACCOUNT_ID,
      NAME, newArrayList(address));
    when(restTemplate.getForEntity("/accounts/account-123", AccountsResponse.class))
      .thenReturn(ResponseEntity.ok(accountsResponse));

    ReserveProductRequest request = new ReserveProductRequest(ORDER_ID, 2, BigDecimal.TEN);

    when(restTemplate.postForEntity(eq("/products/prod-123/reserve"), refEq(request), eq(ReserveProductResponse.class)))
      .thenThrow(HttpClientErrorException.create(UNPROCESSABLE_ENTITY, PLACEHOLDER_ERROR_MESSAGE,
        EMPTY, PLACEHOLDER_ERROR_MESSAGE.getBytes(), defaultCharset()));

    orderService.placeOrder(ACCOUNT_ID, PRODUCT_ID, 2,
      BigDecimal.TEN, ADDRESS_ID);
  }

  @Test(expected = HttpClientErrorException.class)
  public void shouldThrowHttpClientErrorExceptionItselfIfErrorStatusCodeIsOtherThanNotFoundDuringReservation() {
    when(orderIdGenerator.generateOrderId()).thenReturn(ORDER_ID);

    Address address = new Address(ADDRESS_ID, LINE_ONE,
      LINE_TWO, CITY, STATE, COUNTRY, PIN_CODE);
    AccountsResponse accountsResponse = new AccountsResponse(ACCOUNT_ID,
      NAME, newArrayList(address));
    when(restTemplate.getForEntity("/accounts/account-123", AccountsResponse.class))
      .thenReturn(ResponseEntity.ok(accountsResponse));

    ReserveProductRequest request = new ReserveProductRequest(ORDER_ID, 2, BigDecimal.TEN);

    when(restTemplate.postForEntity(eq("/products/prod-123/reserve"), refEq(request), eq(ReserveProductResponse.class)))
      .thenThrow(HttpClientErrorException.create(INTERNAL_SERVER_ERROR, PLACEHOLDER_ERROR_MESSAGE,
        EMPTY, PLACEHOLDER_ERROR_MESSAGE.getBytes(), defaultCharset()));

    orderService.placeOrder(ACCOUNT_ID, PRODUCT_ID, 2,
      BigDecimal.TEN, ADDRESS_ID);
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