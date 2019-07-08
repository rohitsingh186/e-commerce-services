package com.raga.ecommerce.inventory.repository;

import com.raga.ecommerce.inventory.vo.Product;
import com.raga.ecommerce.inventory.vo.ProductReservationLockLog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ProductReservationLockLogRepository extends MongoRepository<ProductReservationLockLog, String> {

  Optional<Product> findByProductId(String productId);
}
