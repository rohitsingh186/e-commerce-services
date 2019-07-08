package com.raga.ecommerce.order.service;

import com.raga.ecommerce.order.exception.AccountNotFoundException;
import com.raga.ecommerce.order.exception.ItemReservationFailedException;
import com.raga.ecommerce.order.exception.ShippingAddressNotFoundException;
import com.raga.ecommerce.order.repository.OrderRepository;
import com.raga.ecommerce.order.service.request.ReserveProductRequest;
import com.raga.ecommerce.order.service.response.AccountsResponse;
import com.raga.ecommerce.order.service.response.ReserveProductResponse;
import com.raga.ecommerce.order.vo.Address;
import com.raga.ecommerce.order.vo.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@Service
public class OrderService {

  private static final String GET_ACCOUNT_URL = "/accounts/%s";
  private static final String RESERVE_PRODUCT_URL = "/products/%s/reserve";

  private final OrderRepository orderRepository;
  private final RestTemplate restTemplate;
  private final OrderIdGenerator orderIdGenerator;

  @Autowired
  public OrderService(OrderRepository orderRepository, RestTemplate restTemplate, OrderIdGenerator orderIdGenerator) {
    this.orderRepository = orderRepository;
    this.restTemplate = restTemplate;
    this.orderIdGenerator = orderIdGenerator;
  }

  public String placeOrder(String accountId, String productId, int quantity,
                           BigDecimal expectedPricePerItem, String shippingAddressId) {

    Address shippingAddress = getShippingAddress(accountId, shippingAddressId);

    return reserveItems(accountId, productId, quantity, expectedPricePerItem, shippingAddress);
  }

  private Address getShippingAddress(String accountId, String shippingAddressId) {
    AccountsResponse accountsResponse = getAccount(accountId);

    return extractShippingAddress(accountId, accountsResponse, shippingAddressId);
  }

  private AccountsResponse getAccount(String accountId) {
    try {
      ResponseEntity<AccountsResponse> accountsResponse = restTemplate.getForEntity(
        buildGetAccountUrl(accountId), AccountsResponse.class);

      return Objects.requireNonNull(accountsResponse.getBody());

    } catch (HttpClientErrorException e) {

      if (NOT_FOUND == e.getStatusCode()) {
        throw new AccountNotFoundException(accountId);
      }

      throw e;
    }
  }

  private Address extractShippingAddress(String accountId, AccountsResponse accountsResponse,
                                         String shippingAddressId) {
    return accountsResponse.getAddresses()
      .stream()
      .filter(address -> addressIdMatches(address, shippingAddressId))
      .findFirst()
      .orElseThrow(() -> new ShippingAddressNotFoundException(accountId, shippingAddressId));
  }

  private boolean addressIdMatches(Address address, String shippingAddressId) {
    return address.getAddressId().equals(shippingAddressId);
  }

  private String reserveItems(String accountId, String productId,
                              int quantity, BigDecimal expectedPricePerItem, Address shippingAddress) {
    String orderId = orderIdGenerator.generateOrderId();

    ReserveProductResponse response = reserveItemsInInventory(orderId, productId, quantity, expectedPricePerItem);

    createAndSaveOrder(accountId, orderId, productId, response.getItems(), quantity,
      response.getCurrentPrice(), shippingAddress);

    return orderId;
  }

  private ReserveProductResponse reserveItemsInInventory(String orderId, String productId,
                                                         int quantity, BigDecimal expectedPricePerItem) {
    try {
      ReserveProductRequest request = new ReserveProductRequest(orderId, quantity, expectedPricePerItem);

      ResponseEntity<ReserveProductResponse> response = restTemplate.postForEntity(
        buildReserveProductUrl(productId), request, ReserveProductResponse.class);

      return Objects.requireNonNull(response.getBody());

    } catch (HttpClientErrorException e) {

      if (UNPROCESSABLE_ENTITY == e.getStatusCode()) {
        throw new ItemReservationFailedException(e.getMessage());
      }

      throw e;
    }
  }

  private void createAndSaveOrder(String accountId, String orderId, String productId, List<String> items,
                                  int quantity, BigDecimal currentPrice, Address shippingAddress) {

    BigDecimal billAmount = currentPrice.multiply(BigDecimal.valueOf(quantity));
    LocalDateTime orderTimestamp = LocalDateTime.now();

    Order order = new Order(orderId, accountId, productId, items, billAmount, orderTimestamp, shippingAddress);

    orderRepository.save(order);
  }

  private String buildGetAccountUrl(String accountId) {
    return String.format(GET_ACCOUNT_URL, accountId);
  }

  private String buildReserveProductUrl(String productId) {
    return String.format(RESERVE_PRODUCT_URL, productId);
  }
}
