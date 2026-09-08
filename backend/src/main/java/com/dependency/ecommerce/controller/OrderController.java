package com.dependency.ecommerce.controller;

import com.dependency.ecommerce.dto.CreateOrderRequest;
import com.dependency.ecommerce.dto.OrderResponse;
import com.dependency.ecommerce.facade.api.OrderFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/orders")
public class OrderController  {
    private final OrderFacade orderFacade;

    public OrderController(OrderFacade orderFacade){
        this.orderFacade = orderFacade;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestParam UUID cartId,
                                                     @RequestParam(required = false) UUID couponId,
                                                     @RequestBody CreateOrderRequest request){
        return ResponseEntity.ok(orderFacade.createOrder( cartId, couponId, request));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@RequestParam UUID userId, @PathVariable UUID orderId){
        log.info("my order items");
        return ResponseEntity.ok(orderFacade.getOrderById(userId, orderId));
    }

    @GetMapping("/getAllOrders")
    public ResponseEntity<List<OrderResponse>> getAllOrders(@RequestParam UUID userId){
        return ResponseEntity.ok(orderFacade.getAllOrders(userId));
    }

    @GetMapping("/user/{targetUserId}")
    public ResponseEntity<List<OrderResponse>> getOrderByUser(@RequestParam UUID userId, @PathVariable UUID targetUserId){
        return ResponseEntity.ok(orderFacade.getOrdersByUser(userId, targetUserId));
    }
}
