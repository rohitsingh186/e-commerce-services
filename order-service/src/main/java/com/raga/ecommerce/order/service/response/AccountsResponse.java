package com.raga.ecommerce.order.service.response;

import com.raga.ecommerce.order.vo.Address;

import java.util.List;

public class AccountsResponse {

  private String accountId;
  private String name;
  private List<Address> addresses;

  public AccountsResponse(String accountId, String name, List<Address> addresses) {
    this.accountId = accountId;
    this.name = name;
    this.addresses = addresses;
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
}
