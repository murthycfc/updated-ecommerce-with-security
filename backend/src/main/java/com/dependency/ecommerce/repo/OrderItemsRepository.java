package com.dependency.ecommerce.repo;

import com.dependency.ecommerce.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderItemsRepository extends JpaRepository<OrderItem, UUID> {

}
