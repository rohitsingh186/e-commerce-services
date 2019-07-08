package com.raga.ecommerce.order.web.response;

public class Error {

  private String code;
  private String title;
  private String message;

  public Error(String code, String title, String message) {
    this.code = code;
    this.title = title;
    this.message = message;
  }

  public String getCode() {
    return code;
  }

  public String getTitle() {
    return title;
  }

  public String getMessage() {
    return message;
  }

  public static class ErrorBuilder {

    private String code;
    private String title;
    private String message;

    public ErrorBuilder withCode(String code) {
      this.code = code;
      return this;
    }

    public ErrorBuilder withTitle(String title) {
      this.title = title;
      return this;
    }

    public ErrorBuilder withMessage(String message) {
      this.message = message;
      return this;
    }

    public Error build() {
      return new Error(code, title, message);
    }
  }
}
