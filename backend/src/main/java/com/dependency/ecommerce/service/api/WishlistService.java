package com.dependency.ecommerce.service.api;

import com.dependency.ecommerce.model.Wishlist;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface WishlistService {
    String addProduct(UUID userId, UUID productId);
    List<Wishlist> getWishlist(UUID userId);
    String removeProduct(UUID userId, UUID productId);
}
