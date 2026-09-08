package com.dependency.ecommerce.controller;

import com.dependency.ecommerce.dto.WishlistResponse;
import com.dependency.ecommerce.facade.api.WishlistFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/wishlist")
public class WishlistController {
    private final WishlistFacade wishlistFacade;

    public WishlistController(WishlistFacade wishlistFacade){
        this.wishlistFacade = wishlistFacade;
    }

    @PostMapping("/addToWishlist")
    public ResponseEntity<String> addToWishlist(@RequestParam UUID userId,
                                                @RequestParam UUID productId){
        return ResponseEntity.ok(wishlistFacade.addToWishlist(userId, productId));
    }

    @GetMapping("/getWishlist")
    public ResponseEntity<List<WishlistResponse>> getWishlist(@RequestParam UUID userId){
        return ResponseEntity.ok(wishlistFacade.getWishlist(userId));
    }

    @PutMapping("/removeFromWishlist")
    public ResponseEntity<String> removeFromWishlist(@RequestParam UUID userId,
                                                     @RequestParam UUID productId){
        return ResponseEntity.ok(wishlistFacade.removeFromWishList(userId, productId));
    }

    @PutMapping("/moveToCart")
    public ResponseEntity<String> moveToCart(@RequestParam UUID userId,
                                             @RequestParam UUID productId){
        return ResponseEntity.ok(wishlistFacade.moveToCart(userId, productId));
    }
}

