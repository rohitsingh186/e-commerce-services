package com.raga.ecommerce.inventory.repository;

import com.raga.ecommerce.inventory.vo.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ProductRepository extends MongoRepository<Product, String> {

  Optional<Product> findByProductId(String productId);
}
