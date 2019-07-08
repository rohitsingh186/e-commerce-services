package com.raga.ecommerce.inventory.web;

import com.google.common.collect.ImmutableMap;
import com.raga.ecommerce.inventory.exception.ProductAvailableInLessQuantityException;
import com.raga.ecommerce.inventory.exception.ProductPriceIncreasedException;
import com.raga.ecommerce.inventory.exception.ProductUnavailableException;
import com.raga.ecommerce.inventory.exception.ProductsException;
import com.raga.ecommerce.inventory.web.response.Error;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@ControllerAdvice
public class DefaultExceptionHandler {

  private static final String ERRORS_FIELD = "errors";

  @ResponseBody
  @ExceptionHandler(value = ProductUnavailableException.class)
  @ResponseStatus(code = UNPROCESSABLE_ENTITY)
  public Map<String, List<Error>> handle(ProductUnavailableException e) {
    return buildErrors("2001", e);
  }

  @ResponseBody
  @ExceptionHandler(value = ProductAvailableInLessQuantityException.class)
  @ResponseStatus(code = UNPROCESSABLE_ENTITY)
  public Map<String, List<Error>> handle(ProductAvailableInLessQuantityException e) {
    return buildErrors("2002", e);
  }

  @ResponseBody
  @ExceptionHandler(value = ProductPriceIncreasedException.class)
  @ResponseStatus(code = UNPROCESSABLE_ENTITY)
  public Map<String, List<Error>> handle(ProductPriceIncreasedException e) {
    return buildErrors("2003", e);
  }

  private Map<String, List<Error>> buildErrors(String code, ProductsException e) {
    List<Error> errors = new ArrayList<>();

    Error error = new Error.ErrorBuilder()
      .withCode(code)
      .withTitle(e.getTitle())
      .withMessage(e.getMessage())
      .build();

    errors.add(error);

    return ImmutableMap.of(ERRORS_FIELD, errors);
  }
}
