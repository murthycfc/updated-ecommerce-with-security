package com.dependency.ecommerce.facade.impl;

import com.dependency.ecommerce.dto.CouponAppliedResponse;
import com.dependency.ecommerce.dto.CreateCouponRequest;
import com.dependency.ecommerce.dto.CouponResponse;
import com.dependency.ecommerce.dto.UpdateCouponRequest;
import com.dependency.ecommerce.facade.api.CouponFacade;
import com.dependency.ecommerce.service.api.CouponService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class CouponFacadeImpl implements CouponFacade {

    private final CouponService couponService;

    public CouponFacadeImpl(CouponService couponService){
        this.couponService = couponService;
    }

    @Override
    public String createCoupon(CreateCouponRequest request, UUID userId) {
        return couponService.createCoupon(request, userId);
    }

    @Override
    public CouponAppliedResponse applyCoupon(UUID id, UUID cartId) {
        return couponService.applyCoupon(id, cartId);
    }

    @Override
    public List<CouponResponse> getAllCoupons() {
        return couponService.getAllCoupons();
    }

    @Override
    public CouponResponse getCouponById(UUID id, UUID userId) {
        return couponService.getCouponById(id, userId);
    }

    @Override
    public String updateCoupon(UpdateCouponRequest request, UUID id, UUID userId) {
        return couponService.updateCoupon(request, id, userId);
    }

    @Override
    public String deleteCoupon(UUID id, UUID userId) {
        return couponService.deleteCoupon(id, userId);
    }


}
