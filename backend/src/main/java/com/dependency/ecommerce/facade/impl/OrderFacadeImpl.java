package com.dependency.ecommerce.facade.impl;

import com.dependency.ecommerce.dto.*;
import com.dependency.ecommerce.exception.AccessDeniedException;
import com.dependency.ecommerce.exception.OrderNotFoundException;
import com.dependency.ecommerce.exception.UserNotFoundException;
import com.dependency.ecommerce.facade.api.OrderFacade;
import com.dependency.ecommerce.model.Order;
import com.dependency.ecommerce.model.User;
import com.dependency.ecommerce.repo.OrderRepository;
import com.dependency.ecommerce.repo.UserRepository;
import com.dependency.ecommerce.service.api.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class OrderFacadeImpl implements OrderFacade {

    private final OrderService orderService;
    private final UserRepository userRepo;
    private final OrderRepository orderRepository;

    public OrderFacadeImpl(OrderService orderService,
                           UserRepository userRepo,
                           OrderRepository orderRepository) {
        this.orderService = orderService;
        this.userRepo = userRepo;
        this.orderRepository = orderRepository;
    }

    @Override
    public OrderResponse createOrder( UUID cartId, UUID couponId, CreateOrderRequest request) {
        return orderService.createOrder(cartId, couponId, request);
    }

    @Override
    public OrderResponse  getOrderById(UUID requestingUserId, UUID orderId) {

        User requestingUser = getUser(requestingUserId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.error("Order not found with ID: {}", orderId);
                    return new OrderNotFoundException("Order not found");
                });

        if(!isAdmin(requestingUser)){
            boolean isOwner = orderRepository.findOrderByIdAndUserIdQuery(requestingUserId.toString(), orderId.toString()).isPresent();
            if(!isOwner){
                log.error("You don't have access to fetch this order with requesting user id: {} order id: {}",requestingUserId, orderId);
                throw new AccessDeniedException("Access denied. You can only access your own orders.");
            }
        }
        return orderService.getOrderById(orderId);
    }

    @Override
    public List<OrderResponse> getAllOrders(UUID requestingUserId) {

        User requestingUser = getUser(requestingUserId);
        checkAdmin(requestingUser);
        return orderService.getAllOrders();
    }

    @Override
    public List<OrderResponse> getOrdersByUser(UUID requestingUserId,UUID targetUserId) {

        User requestingUser = getUser(requestingUserId);
        if (!isAdmin(requestingUser) && !requestingUserId.equals(targetUserId)) {
            throw new AccessDeniedException(
                    "Access denied. You can only access your own orders."
            );
        }
        return orderService.getOrdersByUser(targetUserId);
    }

    // Helper methods
    private User getUser(UUID userId){
        return userRepo.findById(userId)
                .orElseThrow(()->
                        new UserNotFoundException("User not found")
                        );
    }

    private boolean isAdmin(User user){
        return "ADMIN".equals(user.getRole());
    }

    private void checkAdmin(User user){
        if(!isAdmin(user)){
            throw new AccessDeniedException("Access denied. Only admin can perform this operation.");
        }
    }
}
