package com.raga.ecommerce.account.vo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document
public class Account {

  @Id
  private String accountId;
  private String name;
  private List<Address> addresses;

  public Account(String accountId, String name) {
    this.accountId = accountId;
    this.name = name;
    this.addresses = new ArrayList<>();
  }

  public String getAccountId() {
    return accountId;
  }

  public String getName() {
    return name;
  }

  public List<Address> getAddresses() {
    return addresses;
  }

  public void addAddress(String addressId, String lineOne, String lineTwo, String city,
                         String state, String country, String pinCode) {
    Address address = new Address(addressId, lineOne, lineTwo, city, state, country, pinCode);

    this.addresses.add(address);
  }
}
