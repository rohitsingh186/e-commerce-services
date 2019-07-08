package com.raga.ecommerce.account.web.request;

public class AddAddressRequest {

  private final String lineOne;
  private final String lineTwo;
  private final String city;
  private final String state;
  private final String country;
  private final String pinCode;

  public AddAddressRequest(String lineOne, String lineTwo, String city,
                           String state, String country, String pinCode) {
    this.lineOne = lineOne;
    this.lineTwo = lineTwo;
    this.city = city;
    this.state = state;
    this.country = country;
    this.pinCode = pinCode;
  }

  public String getLineOne() {
    return lineOne;
  }

  public String getLineTwo() {
    return lineTwo;
  }

  public String getCity() {
    return city;
  }

  public String getState() {
    return state;
  }

  public String getCountry() {
    return country;
  }

  public String getPinCode() {
    return pinCode;
  }
}
