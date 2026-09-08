package com.dependency.ecommerce.service.impl;

import com.dependency.ecommerce.dto.*;
import com.dependency.ecommerce.enums.DiscountType;
import com.dependency.ecommerce.exception.*;
import com.dependency.ecommerce.model.*;
import com.dependency.ecommerce.repo.*;
import com.dependency.ecommerce.service.api.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepo;
    private final ProductRepository productRepo;
    private final CouponRepository couponRepository;
    private final CartRepository cartRepository;
    private final CartItemsRepository cartItemsRepository;

    OrderServiceImpl(OrderRepository orderRepo,
                     ProductRepository productRepo,
                     CouponRepository couponRepository,
                     CartRepository cartRepository,
                     CartItemsRepository cartItemsRepository) {
        this.orderRepo = orderRepo;
        this.productRepo = productRepo;
        this.couponRepository = couponRepository;
        this.cartRepository = cartRepository;
        this.cartItemsRepository = cartItemsRepository;
    }

    @Override
    public OrderResponse createOrder(UUID cartId, UUID couponId, CreateOrderRequest request) {
        try {
            Order order = new Order();
            order.setOrderNumber(generateOrderNumber());
            BeanUtils.copyProperties(request, order);
            Coupon coupon = null;

            if(couponId!=null){
                coupon = couponRepository.isValidCoupon(couponId.toString())
                        .orElseThrow(()->{
                            log.error("Invalid coupon. So can't apply");
                            return new InValidCouponException("Invalid coupon");
                        });
            }


            Cart cart = cartRepository.findCartById(cartId.toString())
                    .orElseThrow(()->{
                        log.error("No cart, cartId: {}",cartId);
                        return new CartNotFoundException("You don't have cart");
                    });

            User user = cart.getUser();
            order.setUser(user);

            BigDecimal subTotal = BigDecimal.ZERO;
            List<OrderItem> orderItems = new ArrayList<>();
            List<CartItem> activeCartItems = cartItemsRepository.findActiveCartItems(cartId.toString());
            for(CartItem cartItem : activeCartItems){

                // order items
                Product product = productRepo.findByIdAndIsDeletedFalseQuery(cartItem.getProduct().getId().toString())
                        .orElseThrow(() -> {
                            log.error("Product not found while creating order: {}", cartItem.getProduct().getId());
                            return new ProductNotFoundException(
                                    "Product not found");
                        });
                OrderItem orderItem = new OrderItem();

                orderItem.setProduct(product);
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setPrice(product.getPrice());
                orderItem.setOrder(order);
                orderItems.add(orderItem);

                // subtotal
                BigDecimal price = cartItem.getProduct().getPrice();
                int quantity = cartItem.getQuantity();
                subTotal = subTotal.add(price.multiply(BigDecimal.valueOf(quantity)));
            }

            order.setOrderItems(orderItems);

            BigDecimal appliedDiscount;
            if(couponId==null){
                appliedDiscount = BigDecimal.ZERO;
            }
            else{
                appliedDiscount = calculateAppliedDiscount(subTotal, coupon);
            }

            //discount
            order.setSubTotal(subTotal);
            order.setDiscountApplied(appliedDiscount);
            BigDecimal finalTotal = subTotal.subtract(appliedDiscount);
            order.setFinalTotal(finalTotal);

            order.setUser(user);

            Order savedOrder = orderRepo.save(order);

            OrderResponse response = new OrderResponse();
            BeanUtils.copyProperties(savedOrder, response);

            //user
            UserResponse userResponse = new UserResponse();
            BeanUtils.copyProperties(savedOrder.getUser(), userResponse);
            response.setUser(userResponse);

            //order items
            List<OrderItemResponse> itemResponses = new ArrayList<>();
            for(OrderItem item : savedOrder.getOrderItems()){
                OrderItemResponse itemResponse = new OrderItemResponse();
                BeanUtils.copyProperties(item, itemResponse);

                Product product = item.getProduct();
                ProductResponse productResponse = new ProductResponse();
                BeanUtils.copyProperties(product, productResponse);

                itemResponse.setProduct(productResponse);

                itemResponses.add(itemResponse);
            }
            response.setOrderItems(itemResponses);

            return response;
        } catch (ProductNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected error while creating order for userId: {}", cartId, ex);
            throw new OrderServiceException("Unable to create order");
        }
    }

    protected BigDecimal calculateAppliedDiscount(BigDecimal subTotal, Coupon coupon) {
        if(coupon==null){
            return BigDecimal.ZERO;
        }
        if(coupon.getMinOrderAmount().compareTo(subTotal)>0){
            log.error("Coupon can't apply at this price");
            throw new CouponNotApplicableException("Coupon can't apply at this price");
        }
        BigDecimal appliedDiscount;
        if(coupon.getDiscountType() == DiscountType.PERCENTAGE){
            appliedDiscount = subTotal.multiply(coupon.getValue()).divide(BigDecimal.valueOf(100),2, RoundingMode.HALF_UP);
        }
        else if(coupon.getDiscountType() == DiscountType.AMOUNT){
            appliedDiscount = coupon.getValue();
        }
        else{
            throw new IllegalArgumentException("Invalid discount type");
        }
        return appliedDiscount;
    }

    private Long generateOrderNumber() {
        try {
            Optional<Order> lastOrder = orderRepo.getLatestOrderNumber();
            if (lastOrder.isEmpty() || lastOrder.get().getOrderNumber() == null) {
                return 100000L;
            }
            return lastOrder.get().getOrderNumber() + 1;
        } catch (Exception ex) {
            log.error("Failed to generate order number", ex);
            throw new OrderServiceException("Unable to generate order number");
        }
    }




    @Override
    public OrderResponse getOrderById(UUID orderId) {
        try {
            Order order = orderRepo.findByOrderId(orderId.toString())
                    .orElseThrow(() -> {
                        log.error("Order not found with ID: {}", orderId);
                        return new OrderNotFoundException("Order not found");
                    });

            OrderResponse orderResponse = new OrderResponse();
            BeanUtils.copyProperties(order, orderResponse);

            // user
            UserResponse userResponse = new UserResponse();
            User user = order.getUser();
            BeanUtils.copyProperties(user, userResponse);
            orderResponse.setUser(userResponse);

            // order items
            List<OrderItemResponse> itemResponses = new ArrayList<>();
            for(OrderItem item : order.getOrderItems()){
                OrderItemResponse itemResponse = new OrderItemResponse();
                BeanUtils.copyProperties(item, itemResponse);

                Product product = item.getProduct();
                ProductResponse productResponse = new ProductResponse();
                BeanUtils.copyProperties(product, productResponse);

                itemResponse.setProduct(productResponse);

                itemResponses.add(itemResponse);
            }
            orderResponse.setOrderItems(itemResponses);
            return orderResponse;

        } catch (OrderNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected error while fetching order: {}", orderId, ex);
            throw new OrderServiceException("Unable to fetch order");
        }
    }

    @Override
    public List<OrderResponse> getAllOrders() {
        try {
            List<Order> orders = orderRepo.findAllOrders();
            List<OrderResponse> responses = new ArrayList<>();

            for(Order order : orders){
                OrderResponse response = new OrderResponse();
                BeanUtils.copyProperties(order, response);

                UserResponse userResponse = new UserResponse();
                User user = order.getUser();
                BeanUtils.copyProperties(user, userResponse);
                response.setUser(userResponse);

                List<OrderItemResponse> itemResponses = new ArrayList<>();
                for(OrderItem item : order.getOrderItems()){
                    OrderItemResponse itemResponse = new OrderItemResponse();
                    BeanUtils.copyProperties(item, itemResponse);

                    Product product = item.getProduct();
                    ProductResponse productResponse = new ProductResponse();
                    BeanUtils.copyProperties(product, productResponse);

                    itemResponse.setProduct(productResponse);

                    itemResponses.add(itemResponse);
                }
                response.setOrderItems(itemResponses);
                responses.add(response);
            }
            return responses;
        } catch (Exception ex) {
            log.error("Unexpected error while fetching all orders", ex);
            throw new OrderServiceException("Unable to fetch all orders");
        }
    }

    @Override
    public List<OrderResponse> getOrdersByUser(UUID userId) {
        try {
            List<Order> orders = orderRepo.findByUserId(userId.toString());
            List<OrderResponse> responses = new ArrayList<>();

            for(Order order : orders){
                OrderResponse response = new OrderResponse();

                BeanUtils.copyProperties(order, response);

                UserResponse userResponse = new UserResponse();
                User user = order.getUser();
                BeanUtils.copyProperties(user, userResponse);
                response.setUser(userResponse);

                List<OrderItemResponse> itemResponses = new ArrayList<>();
                for(OrderItem item : order.getOrderItems()){
                    OrderItemResponse itemResponse = new OrderItemResponse();
                    BeanUtils.copyProperties(item, itemResponse);

                    Product product = item.getProduct();
                    ProductResponse productResponse = new ProductResponse();
                    BeanUtils.copyProperties(product, productResponse);

                    itemResponse.setProduct(productResponse);

                    itemResponses.add(itemResponse);
                }
                response.setOrderItems(itemResponses);
                responses.add(response);
            }
            return responses;

        } catch (Exception ex) {
            log.error("Unexpected error while fetching orders for user: {}", userId, ex);
            throw new OrderServiceException("Unable to fetch orders for user");
        }
    }
}
