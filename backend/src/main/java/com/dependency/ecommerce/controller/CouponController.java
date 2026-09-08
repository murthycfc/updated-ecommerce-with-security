package com.dependency.ecommerce.controller;

import com.dependency.ecommerce.dto.CouponAppliedResponse;
import com.dependency.ecommerce.dto.CreateCouponRequest;
import com.dependency.ecommerce.dto.CouponResponse;
import com.dependency.ecommerce.dto.UpdateCouponRequest;
import com.dependency.ecommerce.facade.api.CouponFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/coupon")
public class CouponController {

    private final CouponFacade couponFacade;
    CouponController(CouponFacade couponFacade){
        this.couponFacade = couponFacade;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createCoupon(@RequestBody CreateCouponRequest request, @RequestParam UUID userId){
        return ResponseEntity.ok(couponFacade.createCoupon(request, userId));
    }

    @GetMapping("/apply/{id}")
    public ResponseEntity<CouponAppliedResponse> applyCoupon(@PathVariable  UUID id,
                                                             @RequestParam UUID cartId){
        return ResponseEntity.ok(couponFacade.applyCoupon(id, cartId));
    }

    @GetMapping("fetchAll")
    public ResponseEntity<List<CouponResponse>> getAllCoupons(){
        return ResponseEntity.ok(couponFacade.getAllCoupons());
    }

    @GetMapping("/getCouponDetails/{id}")
    public ResponseEntity<CouponResponse> getCouponById(@PathVariable UUID id, @RequestParam UUID userId){
        return ResponseEntity.ok(couponFacade.getCouponById(id, userId));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateCoupon(@RequestBody UpdateCouponRequest request,
                                               @PathVariable UUID id,
                                               @RequestParam UUID userId){
        return ResponseEntity.ok(couponFacade.updateCoupon(request, id, userId));
    }

    @PutMapping("/delete/{id}")
    public ResponseEntity<String> deleteCoupon(@PathVariable UUID id,
                                               @RequestParam UUID userId){
        return ResponseEntity.ok(couponFacade.deleteCoupon(id, userId));
    }

}
