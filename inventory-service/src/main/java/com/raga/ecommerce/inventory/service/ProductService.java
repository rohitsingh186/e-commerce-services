package com.raga.ecommerce.inventory.service;

import com.raga.ecommerce.inventory.exception.ProductAvailableInLessQuantityException;
import com.raga.ecommerce.inventory.exception.ProductPriceIncreasedException;
import com.raga.ecommerce.inventory.exception.ProductUnavailableException;
import com.raga.ecommerce.inventory.lock.ProductReservationLockManager;
import com.raga.ecommerce.inventory.repository.ProductRepository;
import com.raga.ecommerce.inventory.vo.Item;
import com.raga.ecommerce.inventory.vo.Product;
import com.raga.ecommerce.inventory.web.response.ReserveProductResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

@Service
public class ProductService {

  private final ProductRepository productRepository;
  private final ProductReservationLockManager productReservationLockManager;

  @Autowired
  public ProductService(ProductRepository productRepository,
                        ProductReservationLockManager productReservationLockManager) {
    this.productRepository = productRepository;
    this.productReservationLockManager = productReservationLockManager;
  }

  public List<Product> getAvailableProducts() {
    return productRepository.findAll()
      .stream()
      .filter(product -> !product.getUnreservedItems().isEmpty())
      .collect(Collectors.toList());
  }

  /**
   * Gets a lock based on product id and locks it so that only one request
   * for the same product can be reserved at once. Other products can be
   * reserved at the same time by different threads.
   * TODO: Implement mechanism to reserve item in case of multiple JVMs
   **/
  public ReserveProductResponse reserveItems(String orderId, String productId, int quantity, BigDecimal expectedPrice) {

    Lock lock = productReservationLockManager.getLock(productId);

    lock.lock();

    try {
      Optional<Product> product = productRepository.findByProductId(productId);

      if (!product.isPresent() || product.get().getUnreservedItems().isEmpty()) {
        throw new ProductUnavailableException(productId);
      }

      Product existingProduct = product.get();
      int availableQuantity = existingProduct.getUnreservedItems().size();

      if (availableQuantity < quantity) {
        throw new ProductAvailableInLessQuantityException(productId, quantity, availableQuantity);
      }

      if (existingProduct.getPrice().compareTo(expectedPrice) > 0) {
        throw new ProductPriceIncreasedException(productId, expectedPrice, existingProduct.getPrice());
      }

      List<String> items = selectItems(orderId, quantity, existingProduct);

      return new ReserveProductResponse(items, existingProduct.getPrice());

    } finally {
      lock.unlock();
    }
  }

  private List<String> selectItems(String orderId, int quantity, Product product) {
    List<String> itemsSelected = new ArrayList<>();

    for (int i = 0; i < quantity; i++) {
      Item firstUnreservedItem = product.getUnreservedItems().get(0);
      String itemId = firstUnreservedItem.getItemId();
      itemsSelected.add(itemId);
      firstUnreservedItem.reserve(orderId);
    }

    productRepository.save(product);
    return itemsSelected;
  }
}
