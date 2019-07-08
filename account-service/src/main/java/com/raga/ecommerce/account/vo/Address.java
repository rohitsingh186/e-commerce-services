package com.raga.ecommerce.account.vo;

public class Address {

  private String addressId;
  private String lineOne;
  private String lineTwo;
  private String city;
  private String state;
  private String country;
  private String pinCode;

  public Address(String addressId, String lineOne, String lineTwo, String city,
                 String state, String country, String pinCode) {
    this.addressId = addressId;
    this.lineOne = lineOne;
    this.lineTwo = lineTwo;
    this.city = city;
    this.state = state;
    this.country = country;
    this.pinCode = pinCode;
  }

  public String getAddressId() {
    return addressId;
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
