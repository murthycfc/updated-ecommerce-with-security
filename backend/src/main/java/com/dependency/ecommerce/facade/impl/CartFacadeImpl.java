package com.dependency.ecommerce.facade.impl;

import com.dependency.ecommerce.dto.CartResponse;
import com.dependency.ecommerce.facade.api.CartFacade;
import com.dependency.ecommerce.service.api.CartService;
import org.springframework.stereotype.Component;

import java.util.UUID;
@Component
public class CartFacadeImpl implements CartFacade {

    private final CartService cartService;
    public CartFacadeImpl(CartService cartService){
        this.cartService = cartService;
    }

    @Override
    public String  addToCart(UUID userId, UUID productId) {
        cartService.addToCart(userId, productId, 1);
        return "Product added to cart";
    }

    @Override
    public CartResponse getCartByUserId(UUID userId) {
        return cartService.getCartByUserId(userId);
    }

    @Override
    public CartResponse incrementQuantity(UUID userId, UUID productId) {
        return cartService.incrementQuantity(userId, productId);
    }

    @Override
    public CartResponse decrementQuantity(UUID userId, UUID productId) {
        return cartService.decrementQuantity(userId, productId);
    }

    @Override
    public CartResponse removeCartItem(UUID userId, UUID productId) {
        return cartService.removeCartItem(userId, productId);
    }
}
