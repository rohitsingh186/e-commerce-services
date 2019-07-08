package com.raga.ecommerce.order.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.raga.ecommerce.order.exception.AccountNotFoundException;
import com.raga.ecommerce.order.exception.ItemReservationFailedException;
import com.raga.ecommerce.order.exception.ShippingAddressNotFoundException;
import com.raga.ecommerce.order.service.OrderService;
import com.raga.ecommerce.order.web.request.PlaceOrderRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.HttpClientErrorException;

import java.math.BigDecimal;

import static java.nio.charset.Charset.defaultCharset;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.EMPTY;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(OrdersController.class)
public class OrdersControllerTest {

  private static final String PLACEHOLDER_ERROR_MESSAGE = "Hello";
  private static final String ACCOUNT_ID = "account-123";
  private static final String PRODUCT_ID = "prod-123";
  private static final String ADDRESS_ID = "address-123";

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private OrderService orderService;

  @Test
  public void shouldPlaceOrder() throws Exception {
    when(orderService.placeOrder(anyString(), anyString(), anyInt(), any(), any()))
      .thenReturn("order-456");

    PlaceOrderRequest placeOrderRequest = new PlaceOrderRequest(ACCOUNT_ID, PRODUCT_ID,
      2, BigDecimal.valueOf(2222.25), ADDRESS_ID);
    String jsonRequestBody = jsonRequest(placeOrderRequest);

    mockMvc.perform(
      post("/orders")
        .content(jsonRequestBody)
        .contentType(APPLICATION_JSON_VALUE))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.orderId").value("order-456"));
  }

  @Test
  public void shouldShowErrorIfAccountNotFound() throws Exception {
    when(orderService.placeOrder(eq(ACCOUNT_ID), anyString(), anyInt(), any(), any()))
      .thenThrow(new AccountNotFoundException(ACCOUNT_ID));

    PlaceOrderRequest placeOrderRequest = new PlaceOrderRequest(ACCOUNT_ID, PRODUCT_ID,
      2, BigDecimal.valueOf(2222.25), ADDRESS_ID);
    String jsonRequestBody = jsonRequest(placeOrderRequest);

    mockMvc.perform(
      post("/orders")
        .content(jsonRequestBody)
        .contentType(APPLICATION_JSON_VALUE))
      .andExpect(status().isNotFound())
      .andExpect(jsonPath("$.errors[0].code").value("3001"))
      .andExpect(jsonPath("$.errors[0].title").value("Account Not Found"))
      .andExpect(jsonPath("$.errors[0].message").value("Account not found with id: account-123"));
  }

  @Test
  public void shouldShowErrorIfGetAccountCallFails() throws Exception {
    when(orderService.placeOrder(eq(ACCOUNT_ID), anyString(), anyInt(), any(), any()))
      .thenThrow(HttpClientErrorException.create(INTERNAL_SERVER_ERROR, PLACEHOLDER_ERROR_MESSAGE,
        EMPTY, PLACEHOLDER_ERROR_MESSAGE.getBytes(), defaultCharset()));

    PlaceOrderRequest placeOrderRequest = new PlaceOrderRequest(ACCOUNT_ID, PRODUCT_ID,
      2, BigDecimal.valueOf(2222.25), ADDRESS_ID);
    String jsonRequestBody = jsonRequest(placeOrderRequest);

    mockMvc.perform(
      post("/orders")
        .content(jsonRequestBody)
        .contentType(APPLICATION_JSON_VALUE))
      .andExpect(status().isInternalServerError())
      .andExpect(jsonPath("$.errors[0].code").value("3003"))
      .andExpect(jsonPath("$.errors[0].title").value("Unable to process request"))
      .andExpect(jsonPath("$.errors[0].message").exists());
  }

  @Test
  public void shouldShowErrorIfShippingAddressNotFoundForTheAccount() throws Exception {
    when(orderService.placeOrder(eq(ACCOUNT_ID), anyString(), anyInt(), any(), any()))
      .thenThrow(new ShippingAddressNotFoundException(ACCOUNT_ID, ADDRESS_ID));

    PlaceOrderRequest placeOrderRequest = new PlaceOrderRequest(ACCOUNT_ID, PRODUCT_ID,
      2, BigDecimal.valueOf(2222.25), ADDRESS_ID);
    String jsonRequestBody = jsonRequest(placeOrderRequest);

    mockMvc.perform(
      post("/orders")
        .content(jsonRequestBody)
        .contentType(APPLICATION_JSON_VALUE))
      .andExpect(status().isNotFound())
      .andExpect(jsonPath("$.errors[0].code").value("3004"))
      .andExpect(jsonPath("$.errors[0].title").value("Shipping address not found"))
      .andExpect(jsonPath("$.errors[0].message")
        .value("Shipping address not found with addressId: address-123 and accountId: account-123"));
  }

  @Test
  public void shouldShowErrorIfUnableToReserveItem() throws Exception {
    when(orderService.placeOrder(anyString(), eq(PRODUCT_ID), anyInt(), any(), any()))
      .thenThrow(new ItemReservationFailedException("Product not available with id: prod-123"));

    PlaceOrderRequest placeOrderRequest = new PlaceOrderRequest(ACCOUNT_ID, PRODUCT_ID,
      2, BigDecimal.valueOf(2222.25), ADDRESS_ID);
    String jsonRequestBody = jsonRequest(placeOrderRequest);

    mockMvc.perform(
      post("/orders")
        .content(jsonRequestBody)
        .contentType(APPLICATION_JSON_VALUE))
      .andExpect(status().isUnprocessableEntity())
      .andExpect(jsonPath("$.errors[0].code").value("3002"))
      .andExpect(jsonPath("$.errors[0].title").value("Unable to place order"))
      .andExpect(jsonPath("$.errors[0].message")
        .value("Product not available with id: prod-123"));
  }

  private String jsonRequest(PlaceOrderRequest request) throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
    return objectWriter.writeValueAsString(request);
  }
}