package com.dependency.ecommerce.service.api;

import com.dependency.ecommerce.dto.CouponAppliedResponse;
import com.dependency.ecommerce.dto.CreateCouponRequest;
import com.dependency.ecommerce.dto.CouponResponse;
import com.dependency.ecommerce.dto.UpdateCouponRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface CouponService {
    String createCoupon(CreateCouponRequest request, UUID userId);
    CouponAppliedResponse applyCoupon(UUID id, UUID cartId);
    List<CouponResponse> getAllCoupons();
    CouponResponse getCouponById(UUID id, UUID userId);
    String updateCoupon(UpdateCouponRequest request, UUID id, UUID userId);
    String deleteCoupon(UUID id, UUID userId);
}
