package com.dependency.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private UUID id;

    private Long orderNumber;

    private BigDecimal subTotal;

    private BigDecimal discountApplied;

    private BigDecimal finalTotal;

    private String place;

    private LocalDateTime orderedDate;

    private LocalDate deliveryDate;

    private UserResponse user;

    private List<OrderItemResponse> orderItems;
}
