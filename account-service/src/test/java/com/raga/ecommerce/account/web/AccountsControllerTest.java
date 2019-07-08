package com.raga.ecommerce.account.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.raga.ecommerce.account.exception.AccountNotFoundException;
import com.raga.ecommerce.account.service.AccountService;
import com.raga.ecommerce.account.vo.Account;
import com.raga.ecommerce.account.web.request.AddAddressRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(AccountsController.class)
public class AccountsControllerTest {

  private static final String ACCOUNT_ID = "123";
  private static final String ADDRESS_ID = "456";

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private AccountService accountService;

  @Test
  public void shouldReturnAccountIfPresent() throws Exception {
    Account account = new Account(ACCOUNT_ID, "John Wayne");
    account.addAddress(ADDRESS_ID, "Flat 230, A4 Building, Marvel Platina",
      "near EON IT Park", "Pune", "Maharashtra", "India", "411014");
    when(accountService.getAccount(refEq("123"))).thenReturn(account);

    mockMvc.perform(
      get("/accounts/123"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.accountId").value(ACCOUNT_ID))
      .andExpect(jsonPath("$.name").value("John Wayne"))
      .andExpect(jsonPath("$.addresses[0].addressId").value(ADDRESS_ID))
      .andExpect(jsonPath("$.addresses[0].lineOne").value("Flat 230, A4 Building, Marvel Platina"))
      .andExpect(jsonPath("$.addresses[0].lineTwo").value("near EON IT Park"))
      .andExpect(jsonPath("$.addresses[0].city").value("Pune"))
      .andExpect(jsonPath("$.addresses[0].state").value("Maharashtra"))
      .andExpect(jsonPath("$.addresses[0].country").value("India"))
      .andExpect(jsonPath("$.addresses[0].pinCode").value("411014"));
  }

  @Test
  public void shouldReturnErrorMessageIfAccountIsNotPresent() throws Exception {
    when(accountService.getAccount(refEq("123"))).thenThrow(new AccountNotFoundException(ACCOUNT_ID));

    mockMvc.perform(
      get("/accounts/123"))
      .andExpect(status().isNotFound())
      .andExpect(jsonPath("$.errors[0].code").value("1001"))
      .andExpect(jsonPath("$.errors[0].title").value("Account Not Found"))
      .andExpect(jsonPath("$.errors[0].message").value("Account not found with id: 123"));
  }

  @Test
  public void shouldAddAddressIfAccountIsPresent() throws Exception {
    AddAddressRequest addAddressRequest = new AddAddressRequest("Flat 230, A4 Building, Marvel Platina",
      "near EON IT Park", "Pune", "Maharashtra", "India", "411014");
    String addAddressRequestJson = jsonRequest(addAddressRequest);

    mockMvc.perform(
      post("/accounts/123/addresses")
        .contentType(APPLICATION_JSON_VALUE)
        .content(addAddressRequestJson))
      .andExpect(status().isCreated());

    verify(accountService, times(1)).addAddress(ACCOUNT_ID,
      "Flat 230, A4 Building, Marvel Platina", "near EON IT Park",
      "Pune", "Maharashtra", "India", "411014");
  }

  @Test
  public void shouldNotAddAddressAndReturnErrorMessageIfAccountIsNotPresent() throws Exception {
    Mockito.doThrow(new AccountNotFoundException("account-123"))
      .when(accountService)
      .addAddress("account-123", "Flat 230, A4 Building, Marvel Platina",
        "near EON IT Park", "Pune", "Maharashtra", "India", "411014");

    AddAddressRequest addAddressRequest = new AddAddressRequest("Flat 230, A4 Building, Marvel Platina",
      "near EON IT Park", "Pune", "Maharashtra", "India", "411014");
    String addAddressRequestJson = jsonRequest(addAddressRequest);

    mockMvc.perform(
      post("/accounts/account-123/addresses")
        .contentType(APPLICATION_JSON_VALUE)
        .content(addAddressRequestJson))
      .andExpect(status().isNotFound())
      .andExpect(jsonPath("$.errors[0].code").value("1001"))
      .andExpect(jsonPath("$.errors[0].title").value("Account Not Found"))
      .andExpect(jsonPath("$.errors[0].message").value("Account not found with id: account-123"));
  }

  private String jsonRequest(AddAddressRequest addAddressRequest) throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
    return objectWriter.writeValueAsString(addAddressRequest);
  }
}