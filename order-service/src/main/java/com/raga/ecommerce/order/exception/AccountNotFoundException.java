package com.raga.ecommerce.order.exception;

public class AccountNotFoundException extends OrdersException {

  private static final String MESSAGE = "Account not found with id: %s";
  private static final String TITLE = "Account Not Found";

  public AccountNotFoundException(String accountId) {
    super(String.format(MESSAGE, accountId));
  }

  @Override
  public String getTitle() {
    return TITLE;
  }
}
