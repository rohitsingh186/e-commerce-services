package com.raga.ecommerce.account.exception;

public class AccountNotFoundException extends AccountsException {

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
