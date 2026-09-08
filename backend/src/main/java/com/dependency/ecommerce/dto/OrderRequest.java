package com.dependency.ecommerce.dto;

import com.dependency.ecommerce.model.OrderItem;
import com.dependency.ecommerce.model.User;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {

    private UUID userId;
    private BigDecimal subTotal;
    private BigDecimal discountPercentage;
    private BigDecimal finalTotal;
    private String place;
    private User user;
    private List<OrderItemRequest > orderItems;
}
