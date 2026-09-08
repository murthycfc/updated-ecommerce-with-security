package com.dependency.ecommerce.service.impl;

import com.dependency.ecommerce.dto.CouponAppliedResponse;
import com.dependency.ecommerce.dto.CreateCouponRequest;
import com.dependency.ecommerce.dto.CouponResponse;
import com.dependency.ecommerce.dto.UpdateCouponRequest;
import com.dependency.ecommerce.exception.*;
import com.dependency.ecommerce.model.Cart;
import com.dependency.ecommerce.model.CartItem;
import com.dependency.ecommerce.model.Coupon;
import com.dependency.ecommerce.repo.CartItemsRepository;
import com.dependency.ecommerce.repo.CartRepository;
import com.dependency.ecommerce.repo.CouponRepository;
import com.dependency.ecommerce.repo.UserRepository;
import com.dependency.ecommerce.service.api.CouponService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
@Slf4j
@Service
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final CartItemsRepository cartItemsRepository;
    private final OrderServiceImpl orderService;

    public CouponServiceImpl(CouponRepository couponRepository,
                             UserRepository userRepository,
                             CartRepository cartRepository,
                             CartItemsRepository cartItemsRepository,
                             OrderServiceImpl orderService){
        this.couponRepository = couponRepository;
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.cartItemsRepository = cartItemsRepository;
        this.orderService = orderService;
    }

    @Override
    public String createCoupon(CreateCouponRequest request, UUID userId) {

        try {
            int result = userRepository.checkIsAdmin(userId.toString());
            boolean isAdmin = result==1;
            if(!isAdmin){
                log.error("User can't add coupon. userId: {}", userId);
                throw new AccessDeniedException("You don't have access");
            }
            Coupon coupon = new Coupon();
            BeanUtils.copyProperties(request, coupon);
            couponRepository.save(coupon);
            return "Coupon created";
        }
        catch (AccessDeniedException ex){
            throw ex;
        }
        catch (Exception ex){
            log.error("Unexpected error while creating coupon");
            throw new CouponServiceException("Failed to create coupon");
        }
    }

    @Override
    public CouponAppliedResponse applyCoupon(UUID id, UUID cartId) {
        try {
            Coupon coupon = couponRepository.isValidCoupon(id.toString())
                    .orElseThrow(()->{
                        log.error("Invalid coupon. So can't apply");
                        return new InValidCouponException("Invalid coupon");
                    });

            Cart cart = cartRepository.findCartById(cartId.toString())
                    .orElseThrow(()->{
                        log.error("No cart, can't apply coupon");
                        return new CartNotFoundException("You don't have cart");
                    });

            BigDecimal subTotal = BigDecimal.ZERO;
            List<CartItem> activeCartItems = cartItemsRepository.findActiveCartItems(cart.getId().toString());
            for(CartItem cartItem : activeCartItems){
                BigDecimal price = cartItem.getProduct().getPrice();
                int quantity = cartItem.getQuantity();
                subTotal = subTotal.add(price.multiply(BigDecimal.valueOf(quantity)));
            }

            BigDecimal appliedDiscount = orderService.calculateAppliedDiscount(subTotal, coupon);

            CouponAppliedResponse couponAppliedResponse = new CouponAppliedResponse();
            couponAppliedResponse.setAppliedDiscount(appliedDiscount);
            couponAppliedResponse.setMessage("Coupon applied");
            return couponAppliedResponse;
        }
        catch (InValidCouponException| IllegalArgumentException| CartNotFoundException | CouponNotApplicableException ex){
            throw ex;
        }
        catch (Exception ex){
            log.error("Unexpected error while applying coupon");
            throw new CouponServiceException("Failed to apply coupon");
        }
    }

    @Override
    public List<CouponResponse> getAllCoupons() {
        try {
            List<Coupon> coupons = couponRepository.fetchAllCoupons();
            List<CouponResponse> responses = new ArrayList<>();
            for(Coupon coupon : coupons){
                CouponResponse couponResponse = new CouponResponse();
                BeanUtils.copyProperties(coupon, couponResponse);
                responses.add(couponResponse);
            }
            return responses;
        }
        catch (AccessDeniedException ex){
            throw ex;
        }
        catch (Exception ex){
            log.error("Unexpected error while fetch all coupon");
            throw new CouponServiceException("Failed to fetch all coupons");
        }
    }

    @Override
    public CouponResponse getCouponById(UUID id, UUID userId) {
        try {
            int result = userRepository.checkIsAdmin(userId.toString());
            boolean isAdmin = result==1;
            if(!isAdmin){
                log.error("User can't fetch the coupon details. userId: {}", userId);
                throw new AccessDeniedException("You don't have access");
            }
            Coupon coupon = couponRepository.getCouponDetails(id.toString())
                    .orElseThrow(()->{
                        log.error("Coupon not found with id: {}", id);
                        return new CouponNotFoundException("Coupon not found");
                    });
            CouponResponse response = new CouponResponse();
            BeanUtils.copyProperties(coupon, response);
            return response;
        }
        catch (AccessDeniedException | CouponNotFoundException ex){
            throw ex;
        }
        catch (Exception ex){
            log.error("Unexpected error while fetch the coupon details");
            throw new CouponServiceException("Failed to fetch coupon details");
        }
    }

    @Override
    public String updateCoupon(UpdateCouponRequest request, UUID id, UUID userId) {
        int result = userRepository.checkIsAdmin(userId.toString());
        boolean isAdmin = result==1;
        if(!isAdmin){
            log.error("User can't update the coupon. userId: {}", userId);
            throw new AccessDeniedException("You don't have access");
        }
        Coupon existingCoupon = couponRepository.getCouponDetails(id.toString())
                .orElseThrow(()->{
                    log.error("Coupon not found with id: {}, can't update", id);
                    return new CouponNotFoundException("Coupon not found");
                });
        BeanUtils.copyProperties(request, existingCoupon);
        couponRepository.save(existingCoupon);
        return "Coupon updated successfully";
    }

    @Override
    public String deleteCoupon(UUID id, UUID userId) {
        int result = userRepository.checkIsAdmin(userId.toString());
        boolean isAdmin = result==1;
        if(!isAdmin){
            log.error("User can't delete the coupon. userId: {}", userId);
            throw new AccessDeniedException("You don't have access");
        }
        Coupon existingCoupon = couponRepository.getCouponDetails(id.toString())
                .orElseThrow(()->{
                    log.error("Coupon not found with id: {}, can't delete", id);
                    return new CouponNotFoundException("Coupon not found");
                });
        existingCoupon.setDeleted(true);
        couponRepository.save(existingCoupon);
        return "Coupon deleted successfully";
    }
}
