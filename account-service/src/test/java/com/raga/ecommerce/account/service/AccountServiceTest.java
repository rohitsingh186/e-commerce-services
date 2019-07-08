package com.raga.ecommerce.account.service;

import com.raga.ecommerce.account.exception.AccountNotFoundException;
import com.raga.ecommerce.account.repository.AccountRepository;
import com.raga.ecommerce.account.vo.Account;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AccountServiceTest {

  private static final String ACCOUNT_ID = "123";

  @Mock
  private AccountRepository accountRepository;

  @Mock
  private IdGenerator idGenerator;

  private AccountService accountService;

  @Before
  public void setUp() {
    accountService = new AccountService(accountRepository, idGenerator);
  }

  @Test
  public void shouldReturnAccountIfPresent() {
    Account account = new Account(ACCOUNT_ID, "John Wayne");
    when(accountRepository.findByAccountId(ACCOUNT_ID)).thenReturn(Optional.of(account));

    Account actual = accountService.getAccount(ACCOUNT_ID);
    assertThat(actual).isEqualTo(account);
  }

  @Test(expected = AccountNotFoundException.class)
  public void shouldThrowExceptionIfAccountNotFound() {
    when(accountRepository.findByAccountId(ACCOUNT_ID)).thenReturn(Optional.empty());

    accountService.getAccount(ACCOUNT_ID);
  }

  @Test
  public void shouldAddAddressIfAccountIsPresent() {
    Account account = new Account(ACCOUNT_ID, "John Wayne");
    when(accountRepository.findByAccountId(ACCOUNT_ID)).thenReturn(Optional.of(account));

    when(idGenerator.generateId()).thenReturn("address-123");

    accountService.addAddress(ACCOUNT_ID, "Flat 230, A4 Building, Marvel Platina",
      "near EON IT Park", "Pune", "Maharashtra", "India", "411014");

    ArgumentCaptor<Account> argumentCaptor = ArgumentCaptor.forClass(Account.class);
    verify(accountRepository, times(1)).save(argumentCaptor.capture());

    Account actual = argumentCaptor.getValue();
    assertThat(actual.getAccountId()).isEqualTo(ACCOUNT_ID);
    assertThat(actual.getName()).isEqualTo("John Wayne");
    assertThat(actual.getAddresses().get(0).getAddressId()).isEqualTo("address-123");
    assertThat(actual.getAddresses().get(0).getLineOne()).isEqualTo("Flat 230, A4 Building, Marvel Platina");
    assertThat(actual.getAddresses().get(0).getLineTwo()).isEqualTo("near EON IT Park");
    assertThat(actual.getAddresses().get(0).getCity()).isEqualTo("Pune");
    assertThat(actual.getAddresses().get(0).getState()).isEqualTo("Maharashtra");
    assertThat(actual.getAddresses().get(0).getCountry()).isEqualTo("India");
    assertThat(actual.getAddresses().get(0).getPinCode()).isEqualTo("411014");
  }

  @Test(expected = AccountNotFoundException.class)
  public void shouldNotSaveAddressAndThrowExceptionIfAccountNotFound() {
    when(accountRepository.findByAccountId(ACCOUNT_ID)).thenReturn(Optional.empty());

    accountService.addAddress(ACCOUNT_ID, "Flat 230, A4 Building, Marvel Platina",
      "near EON IT Park", "Pune", "Maharashtra", "India", "411014");
  }
}