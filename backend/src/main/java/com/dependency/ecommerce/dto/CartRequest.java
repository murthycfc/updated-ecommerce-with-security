package com.dependency.ecommerce.dto;

import com.dependency.ecommerce.model.CartItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartRequest {
    private UUID userId;
    private UUID productId;
    private Integer quantity;
}
