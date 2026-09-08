package com.dependency.ecommerce.facade.api;

import com.dependency.ecommerce.dto.CreateOrderRequest;
import com.dependency.ecommerce.dto.OrderRequest;
import com.dependency.ecommerce.dto.OrderResponse;
import com.dependency.ecommerce.model.Order;

import java.util.List;
import java.util.UUID;

public interface OrderFacade {
    OrderResponse createOrder(UUID cartId, UUID couponId, CreateOrderRequest request);

    OrderResponse getOrderById(UUID requestingUserId, UUID orderId);

    List<OrderResponse> getAllOrders(UUID requestingUserId);

    List<OrderResponse> getOrdersByUser(UUID requestingUserId, UUID targetUserId);
}
