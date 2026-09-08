package com.dependency.ecommerce.facade.api;

import com.dependency.ecommerce.dto.CartResponse;
import com.dependency.ecommerce.model.Cart;

import java.util.List;
import java.util.UUID;

public interface CartFacade {
    String addToCart(UUID userId, UUID productId);
    CartResponse getCartByUserId(UUID userId);
    CartResponse incrementQuantity(UUID userId, UUID productId);
    CartResponse decrementQuantity(UUID userId, UUID productId);
    CartResponse removeCartItem(UUID userId, UUID productId);
}
