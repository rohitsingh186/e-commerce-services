package com.raga.ecommerce.order.repository;

import com.raga.ecommerce.order.vo.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderRepository extends MongoRepository<Order, String> {
}
