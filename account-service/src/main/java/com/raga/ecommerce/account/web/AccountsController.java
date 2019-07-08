package com.raga.ecommerce.account.web;

import com.raga.ecommerce.account.service.AccountService;
import com.raga.ecommerce.account.vo.Account;
import com.raga.ecommerce.account.web.request.AddAddressRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping(value = "/accounts")
public class AccountsController {

  private final AccountService accountService;

  @Autowired
  public AccountsController(AccountService accountService) {
    this.accountService = accountService;
  }

  @ResponseStatus(HttpStatus.OK)
  @RequestMapping(value = "/{accountId}", method = GET, produces = APPLICATION_JSON_VALUE)
  public Account getAccount(@PathVariable String accountId) {
    return accountService.getAccount(accountId);
  }

  @ResponseStatus(HttpStatus.CREATED)
  @RequestMapping(value = "/{accountId}/addresses", method = POST, consumes = APPLICATION_JSON_VALUE)
  public void addAddress(@PathVariable String accountId, @RequestBody AddAddressRequest request) {

    accountService.addAddress(accountId, request.getLineOne(), request.getLineTwo(),
      request.getCity(), request.getState(), request.getCountry(), request.getPinCode());
  }
}
