package com.dependency.ecommerce.service.api;

import com.dependency.ecommerce.dto.CartResponse;
import com.dependency.ecommerce.model.Cart;

import java.util.UUID;

public interface CartService {
    void addToCart(UUID userId, UUID productId, Integer quantity);
    CartResponse getCartByUserId(UUID userId);
    CartResponse incrementQuantity(UUID userId, UUID productId);
    CartResponse decrementQuantity(UUID userId, UUID productId);
    CartResponse removeCartItem(UUID userId, UUID productId);
}
