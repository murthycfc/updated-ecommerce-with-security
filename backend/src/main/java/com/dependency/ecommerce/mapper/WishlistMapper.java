package com.dependency.ecommerce.mapper;

import com.dependency.ecommerce.dto.ProductResponse;
import com.dependency.ecommerce.dto.WishlistResponse;
import com.dependency.ecommerce.model.Cart;
import com.dependency.ecommerce.model.Wishlist;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class WishlistMapper {
    public List<WishlistResponse> toWishlistResponse(List<Wishlist> wishlist){
        return wishlist.stream()
                .map(this::toWishlistItemResponse)
                .collect(Collectors.toList());
    }

    private WishlistResponse toWishlistItemResponse(Wishlist wishlist){

        WishlistResponse wishlistResponse = new WishlistResponse();

        wishlistResponse.setId(wishlist.getId());
        wishlistResponse.setUserId(wishlist.getUser().getId());

        ProductResponse productResponse = new ProductResponse();
        BeanUtils.copyProperties(wishlist.getProduct(), productResponse);

        wishlistResponse.setProductResponse(productResponse);

        return wishlistResponse;
    }
}
