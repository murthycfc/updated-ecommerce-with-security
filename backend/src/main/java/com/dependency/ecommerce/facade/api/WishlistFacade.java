package com.dependency.ecommerce.facade.api;

import com.dependency.ecommerce.dto.WishlistResponse;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface WishlistFacade {
    String addToWishlist(UUID userId, UUID productId);
    List<WishlistResponse> getWishlist(UUID userId);
    String removeFromWishList(UUID userId, UUID productId);
    String moveToCart(UUID userId, UUID productId);
 }
