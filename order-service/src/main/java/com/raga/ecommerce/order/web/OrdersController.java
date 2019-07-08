package com.raga.ecommerce.order.web;

import com.google.common.collect.ImmutableMap;
import com.raga.ecommerce.order.service.OrderService;
import com.raga.ecommerce.order.web.request.PlaceOrderRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping(value = "/orders")
public class OrdersController {

  private static final String ORDER_ID_FIELD = "orderId";
  private final OrderService orderService;

  @Autowired
  public OrdersController(OrderService orderService) {
    this.orderService = orderService;
  }

  @RequestMapping(method = POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
  @ResponseStatus(code = CREATED)
  public Map<String, String> placeOrder(@RequestBody PlaceOrderRequest request) {

    String orderId = orderService.placeOrder(request.getAccountId(), request.getProductId(),
      request.getQuantity(), request.getExpectedPricePerItem(), request.getShippingAddressId());

    return jsonResponse(orderId);
  }

  private ImmutableMap<String, String> jsonResponse(String orderId) {
    return ImmutableMap.of(ORDER_ID_FIELD, orderId);
  }
}
