package com.raga.ecommerce.account.service;

import com.raga.ecommerce.account.exception.AccountNotFoundException;
import com.raga.ecommerce.account.repository.AccountRepository;
import com.raga.ecommerce.account.vo.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

  private final AccountRepository accountRepository;
  private final IdGenerator idGenerator;

  @Autowired
  public AccountService(AccountRepository accountRepository, IdGenerator idGenerator) {
    this.accountRepository = accountRepository;
    this.idGenerator = idGenerator;
  }

  public Account getAccount(String accountId) {
    return accountRepository
      .findByAccountId(accountId)
      .orElseThrow(() -> new AccountNotFoundException(accountId));
  }

  public void addAddress(String accountId, String lineOne, String lineTwo, String city,
                         String state, String country, String pinCode) {
    Account existingAccount = getAccount(accountId);

    String addressId = idGenerator.generateId();
    existingAccount.addAddress(addressId, lineOne, lineTwo, city, state, country, pinCode);

    accountRepository.save(existingAccount);
  }
}
