package com.raga.ecommerce.inventory.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.raga.ecommerce.inventory.exception.ProductAvailableInLessQuantityException;
import com.raga.ecommerce.inventory.exception.ProductPriceIncreasedException;
import com.raga.ecommerce.inventory.exception.ProductUnavailableException;
import com.raga.ecommerce.inventory.service.ProductService;
import com.raga.ecommerce.inventory.vo.Product;
import com.raga.ecommerce.inventory.web.request.ReserveProductRequest;
import com.raga.ecommerce.inventory.web.response.ReserveProductResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ProductsController.class)
public class ProductsControllerTest {

  private static final String PRODUCT_ID = "prod-456";
  private static final String ORDER_ID = "order-123";

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private ProductService productService;

  @Test
  public void shouldReturnAvailableProducts() throws Exception {
    Product watch = new Product("prod-123", "Fasttrack Watch", BigDecimal.valueOf(2222.25));
    Product laptop = new Product(PRODUCT_ID, "Lenovo Thinkpad", BigDecimal.valueOf(72222.59));
    List<Product> products = newArrayList(watch, laptop);
    when(productService.getAvailableProducts()).thenReturn(products);

    mockMvc.perform(
      get("/products"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.products[0].productId").value("prod-123"))
      .andExpect(jsonPath("$.products[0].productName").value("Fasttrack Watch"))
      .andExpect(jsonPath("$.products[0].price").value("2222.25"))
      .andExpect(jsonPath("$.products[0].items").doesNotExist())
      .andExpect(jsonPath("$.products[1].productId").value(PRODUCT_ID))
      .andExpect(jsonPath("$.products[1].productName").value("Lenovo Thinkpad"))
      .andExpect(jsonPath("$.products[1].price").value("72222.59"))
      .andExpect(jsonPath("$.products[1].items").doesNotExist());
  }

  @Test
  public void shouldReserveGivenQuantityOfProductIfAvailable() throws Exception {
    List<String> items = newArrayList("item-a1", "item-a2");
    ReserveProductResponse response = new ReserveProductResponse(items, BigDecimal.valueOf(2000.99));
    when(productService.reserveItems(ORDER_ID, PRODUCT_ID, 2,
      BigDecimal.valueOf(2222.25))).thenReturn(response);

    ReserveProductRequest reserveProductRequest = new ReserveProductRequest(ORDER_ID,
      2, BigDecimal.valueOf(2222.25));
    String jsonRequest = jsonRequest(reserveProductRequest);

    mockMvc.perform(
      post("/products/prod-456/reserve")
        .content(jsonRequest)
        .contentType(APPLICATION_JSON_VALUE))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.items[0]").value("item-a1"))
      .andExpect(jsonPath("$.items[1]").value("item-a2"))
      .andExpect(jsonPath("$.currentPrice").value("2000.99"));
  }

  @Test
  public void shouldShowErrorIfProductNotAvailable() throws Exception {
    when(productService.reserveItems(ORDER_ID, PRODUCT_ID, 2,
      BigDecimal.valueOf(2222.25))).thenThrow(new ProductUnavailableException(PRODUCT_ID));

    ReserveProductRequest reserveProductRequest = new ReserveProductRequest(ORDER_ID,
      2, BigDecimal.valueOf(2222.25));
    String jsonRequest = jsonRequest(reserveProductRequest);

    mockMvc.perform(
      post("/products/prod-456/reserve")
        .content(jsonRequest)
        .contentType(APPLICATION_JSON_VALUE))
      .andExpect(status().isUnprocessableEntity())
      .andExpect(jsonPath("$.errors[0].code").value("2001"))
      .andExpect(jsonPath("$.errors[0].title").value("Product Not Available"))
      .andExpect(jsonPath("$.errors[0].message").value("Product not available with id: prod-456"));
  }

  @Test
  public void shouldShowErrorIfProductAvailableInLessQuantity() throws Exception {
    when(productService.reserveItems(ORDER_ID, PRODUCT_ID, 2,
      BigDecimal.valueOf(2222.25))).thenThrow(new ProductAvailableInLessQuantityException(PRODUCT_ID, 2, 1));

    ReserveProductRequest reserveProductRequest = new ReserveProductRequest(ORDER_ID,
      2, BigDecimal.valueOf(2222.25));
    String jsonRequest = jsonRequest(reserveProductRequest);

    mockMvc.perform(
      post("/products/prod-456/reserve")
        .content(jsonRequest)
        .contentType(APPLICATION_JSON_VALUE))
      .andExpect(status().isUnprocessableEntity())
      .andExpect(jsonPath("$.errors[0].code").value("2002"))
      .andExpect(jsonPath("$.errors[0].title").value("Product Available In Less Quantity"))
      .andExpect(jsonPath("$.errors[0].message")
        .value("2 quantity of product with id prod-456 is not available. Available quantity is: 1"));
  }

  @Test
  public void shouldShowErrorIfProductPriceIsMoreThanExpected() throws Exception {
    when(productService.reserveItems(ORDER_ID, PRODUCT_ID, 2,
      BigDecimal.valueOf(2222.25)))
      .thenThrow(new ProductPriceIncreasedException(PRODUCT_ID,
        BigDecimal.valueOf(2222.25), BigDecimal.valueOf(2400.99)));

    ReserveProductRequest reserveProductRequest = new ReserveProductRequest(ORDER_ID,
      2, BigDecimal.valueOf(2222.25));
    String jsonRequest = jsonRequest(reserveProductRequest);

    mockMvc.perform(
      post("/products/prod-456/reserve")
        .content(jsonRequest)
        .contentType(APPLICATION_JSON_VALUE))
      .andExpect(status().isUnprocessableEntity())
      .andExpect(jsonPath("$.errors[0].code").value("2003"))
      .andExpect(jsonPath("$.errors[0].title").value("Product Price More Than Expected"))
      .andExpect(jsonPath("$.errors[0].message")
        .value("Product price is more than expected for product with id: prod-456. " +
          "Expected: 2222.25, Current: 2400.99"));
  }

  private String jsonRequest(ReserveProductRequest request) throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
    return objectWriter.writeValueAsString(request);
  }
}