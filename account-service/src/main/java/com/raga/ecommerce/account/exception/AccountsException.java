package com.raga.ecommerce.account.exception;

public abstract class AccountsException extends RuntimeException {

  public AccountsException(String message) {
    super(message);
  }

  public abstract String getTitle();
}
