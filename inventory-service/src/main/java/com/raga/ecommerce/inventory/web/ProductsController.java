package com.raga.ecommerce.inventory.web;

import com.google.common.collect.ImmutableMap;
import com.raga.ecommerce.inventory.service.ProductService;
import com.raga.ecommerce.inventory.vo.Product;
import com.raga.ecommerce.inventory.vo.ProductResponse;
import com.raga.ecommerce.inventory.web.request.ReserveProductRequest;
import com.raga.ecommerce.inventory.web.response.ReserveProductResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping(value = "/products")
public class ProductsController {

  private static final String PRODUCTS_FIELD = "products";

  private final ProductService productService;

  @Autowired
  public ProductsController(ProductService productService) {
    this.productService = productService;
  }

  @ResponseStatus(HttpStatus.OK)
  @RequestMapping(method = GET, produces = APPLICATION_JSON_VALUE)
  public Map<String, List<ProductResponse>> getProducts() {

    return buildProductResponse(productService.getAvailableProducts());
  }

  @ResponseStatus(HttpStatus.OK)
  @RequestMapping(value = "/{productId}/reserve", method = POST,
    consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
  public ReserveProductResponse reserveItems(@PathVariable String productId,
                                             @RequestBody ReserveProductRequest request) {

    return productService.reserveItems(request.getOrderId(), productId,
      request.getQuantity(), request.getExpectedPrice());
  }

  private Map<String, List<ProductResponse>> buildProductResponse(List<Product> products) {
    List<ProductResponse> productResponses = products.stream()
      .map(ProductResponse::new)
      .collect(Collectors.toList());

    return ImmutableMap.of(PRODUCTS_FIELD, productResponses);
  }
}
