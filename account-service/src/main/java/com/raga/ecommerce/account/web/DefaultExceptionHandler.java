package com.raga.ecommerce.account.web;

import com.google.common.collect.ImmutableMap;
import com.raga.ecommerce.account.exception.AccountNotFoundException;
import com.raga.ecommerce.account.exception.AccountsException;
import com.raga.ecommerce.account.web.response.Error;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@ControllerAdvice
public class DefaultExceptionHandler {

  private static final String ERRORS_FIELD = "errors";

  @ResponseBody
  @ExceptionHandler(value = AccountNotFoundException.class)
  @ResponseStatus(code = NOT_FOUND)
  public Map<String, List<Error>> handle(AccountNotFoundException e) {
    return buildErrors("1001", e);
  }

  private Map<String, List<Error>> buildErrors(String code, AccountsException e) {
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
