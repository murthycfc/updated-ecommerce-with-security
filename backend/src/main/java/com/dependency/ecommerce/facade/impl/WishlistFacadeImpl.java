package com.dependency.ecommerce.facade.impl;

import com.dependency.ecommerce.dto.WishlistResponse;
import com.dependency.ecommerce.facade.api.WishlistFacade;
import com.dependency.ecommerce.mapper.WishlistMapper;
import com.dependency.ecommerce.model.Wishlist;
import com.dependency.ecommerce.service.api.CartService;
import com.dependency.ecommerce.service.api.WishlistService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
public class WishlistFacadeImpl implements WishlistFacade {

    private final WishlistService wishlistService;
    private final WishlistMapper wishlistMapper;
    private final CartService cartService;

    public WishlistFacadeImpl(WishlistService wishlistService,
                              WishlistMapper wishlistMapper,
                              CartService cartService){
        this.wishlistService = wishlistService;
        this.wishlistMapper = wishlistMapper;
        this.cartService = cartService;
    }

    @Override
    public String addToWishlist(UUID userId, UUID productId) {
        return wishlistService.addProduct(userId, productId);
    }

    @Override
    public List<WishlistResponse> getWishlist(UUID userId) {
        List<Wishlist> result = wishlistService.getWishlist(userId);
        return wishlistMapper.toWishlistResponse(result);
    }

    @Override
    public String removeFromWishList(UUID userId, UUID productId) {
        return wishlistService.removeProduct(userId, productId);
    }

    @Transactional
    @Override
    public String moveToCart(UUID userId, UUID productId) {
        cartService.addToCart(userId, productId, 1);
        String removalMsg = wishlistService.removeProduct(userId, productId);
        return "Product moved to cart";
    }
}
