package com.raga.ecommerce.order.web;

import com.google.common.collect.ImmutableMap;
import com.raga.ecommerce.order.exception.AccountNotFoundException;
import com.raga.ecommerce.order.exception.ItemReservationFailedException;
import com.raga.ecommerce.order.exception.OrdersException;
import com.raga.ecommerce.order.exception.ShippingAddressNotFoundException;
import com.raga.ecommerce.order.web.response.Error;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@ControllerAdvice
@ResponseBody
public class DefaultExceptionHandler {

  private static final String ERRORS_FIELD = "errors";

  @ExceptionHandler(value = AccountNotFoundException.class)
  @ResponseStatus(code = NOT_FOUND)
  public Map<String, List<Error>> handle(AccountNotFoundException e) {
    return buildErrors("3001", e);
  }

  @ExceptionHandler(value = ItemReservationFailedException.class)
  @ResponseStatus(code = UNPROCESSABLE_ENTITY)
  public Map<String, List<Error>> handle(ItemReservationFailedException e) {
    return buildErrors("3002", e);
  }

  @ExceptionHandler(value = ShippingAddressNotFoundException.class)
  @ResponseStatus(code = NOT_FOUND)
  public Map<String, List<Error>> handle(ShippingAddressNotFoundException e) {
    return buildErrors("3004", e);
  }

  @ExceptionHandler(value = HttpClientErrorException.class)
  @ResponseStatus(code = INTERNAL_SERVER_ERROR)
  public Map<String, List<Error>> handle(HttpClientErrorException e) {
    return buildErrors("3003", "Unable to process request", e.getMessage());
  }

  private Map<String, List<Error>> buildErrors(String code, OrdersException e) {
    List<Error> errors = new ArrayList<>();
    Error error = buildError(code, e.getTitle(), e.getMessage());
    errors.add(error);
    return ImmutableMap.of(ERRORS_FIELD, errors);
  }

  private Map<String, List<Error>> buildErrors(String code, String title, String message) {
    List<Error> errors = new ArrayList<>();
    Error error = buildError(code, title, message);
    errors.add(error);
    return ImmutableMap.of(ERRORS_FIELD, errors);
  }

  private Error buildError(String code, String title, String message) {
    return new Error.ErrorBuilder()
      .withCode(code)
      .withTitle(title)
      .withMessage(message)
      .build();
  }
}
