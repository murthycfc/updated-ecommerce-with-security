package com.dependency.ecommerce.service.api;

import com.dependency.ecommerce.dto.CreateOrderRequest;
import com.dependency.ecommerce.dto.OrderResponse;
import com.dependency.ecommerce.model.Order;
import com.dependency.ecommerce.model.User;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    OrderResponse createOrder(UUID cartId, UUID couponId, CreateOrderRequest order);
    OrderResponse getOrderById(UUID id);
    List<OrderResponse> getAllOrders();
    List<OrderResponse> getOrdersByUser(UUID userId);
}
