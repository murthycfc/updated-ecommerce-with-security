package com.dependency.ecommerce.controller;

import com.dependency.ecommerce.dto.CartResponse;
import com.dependency.ecommerce.facade.api.CartFacade;
import com.dependency.ecommerce.model.Cart;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
@Slf4j

@RestController
@RequestMapping("/cart")
public class CartController {
    private final CartFacade cartFacade;
    public CartController(CartFacade cartFacade){
        this.cartFacade = cartFacade;
    }

    @PostMapping("/addToCart")
    public ResponseEntity<String> addToCart(@RequestParam UUID userId, @RequestParam UUID productId){
        return ResponseEntity.ok(cartFacade.addToCart(userId,productId));
    }

    @GetMapping("/getCart/{userId}")
    public ResponseEntity<CartResponse> getCartByUserId(@PathVariable UUID userId){
        return ResponseEntity.ok(cartFacade.getCartByUserId(userId));
    }

    @PutMapping("/increment")
    public ResponseEntity<CartResponse> incrementQuantity(
            @RequestParam UUID userId,
            @RequestParam UUID productId){
        return ResponseEntity.ok(cartFacade.incrementQuantity(userId, productId));
    }

    @PutMapping("/decrement")
    public ResponseEntity<CartResponse> decrementQuantity(
            @RequestParam UUID userId,
            @RequestParam UUID productId) {
        return ResponseEntity.ok(
                cartFacade.decrementQuantity(userId, productId));
    }

    @PutMapping("/remove")
    public ResponseEntity<CartResponse> removeCartItem(
            @RequestParam UUID userId,
            @RequestParam UUID productId) {
        return ResponseEntity.ok(
                cartFacade.removeCartItem(userId, productId)
        );
    }
}
