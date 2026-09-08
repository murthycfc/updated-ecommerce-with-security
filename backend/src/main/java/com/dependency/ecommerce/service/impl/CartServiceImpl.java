package com.dependency.ecommerce.service.impl;

import com.dependency.ecommerce.dto.CartItemResponse;
import com.dependency.ecommerce.dto.CartResponse;
import com.dependency.ecommerce.dto.ProductResponse;
import com.dependency.ecommerce.exception.*;
import com.dependency.ecommerce.model.Cart;
import com.dependency.ecommerce.model.CartItem;
import com.dependency.ecommerce.model.Product;
import com.dependency.ecommerce.model.User;
import com.dependency.ecommerce.repo.CartItemsRepository;
import com.dependency.ecommerce.repo.CartRepository;
import com.dependency.ecommerce.repo.ProductRepository;
import com.dependency.ecommerce.repo.UserRepository;
import com.dependency.ecommerce.service.api.CartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Service
public class CartServiceImpl implements CartService {

   private final UserRepository userRepository;
   private final ProductRepository productRepository;
   private final CartRepository cartRepository;
    private final CartItemsRepository cartItemsRepository;

    public CartServiceImpl(UserRepository userRepository,
                          ProductRepository productRepository,
                          CartRepository cartRepository, CartItemsRepository cartItemsRepository){
       this.productRepository = productRepository;
       this.userRepository = userRepository;
       this.cartRepository = cartRepository;
        this.cartItemsRepository = cartItemsRepository;
    }

    @Override
    public void addToCart(UUID userId, UUID productId, Integer quantity) {
       try {
           User user = userRepository.findByIdAndIsDeletedFalse(userId.toString())
                   .orElseThrow(()->{
                       log.error("User not found with id {}", userId);
                       return new UserNotFoundException("User not found");
                   });

           Product product = productRepository.findById(productId.toString())
                   .orElseThrow(()->{
                       log.error("Product not found with id: {}",productId);
                       return new ProductNotFoundException("Product not found");
                   });

           if(product.isDeleted()){
               log.error("Product no longer exists with id: {}", productId);
               throw new ProductInActiveException("Product no longer exists");
           }

           Cart cart = cartRepository.findUserCartQuery(user.getId().toString())
                   .orElseGet(()->{
                      Cart newCart = new Cart();
                      newCart.setUser(user);
                      return cartRepository.save(newCart);
                   });

           Optional<CartItem> existingItemOpt = cartItemsRepository
                   .findActiveCartItem(cart.getId().toString() ,product.getId().toString());
           if(existingItemOpt.isPresent()){
               CartItem existingItem = existingItemOpt.get();
               existingItem.setQuantity(existingItem.getQuantity()+1);
               cartItemsRepository.save(existingItem);

           }
           else{
               CartItem cartItem = new CartItem();
               cartItem.setProduct(product);
               cartItem.setCart(cart);
               cartItem.setQuantity(quantity);

               cart.getCartItems().add(cartItem);
               cartRepository.save(cart);
           }
       }
       catch (UserNotFoundException | ProductNotFoundException | ProductInActiveException ex){
            throw ex;
       }
       catch (Exception ex){
           throw new CartServiceException("An error occurred in cart");
       }
    }

    @Override
    public CartResponse getCartByUserId(UUID userId) {
        try {
            User user = userRepository.findByIdAndIsDeletedFalse(userId.toString())
                    .orElseThrow(()->{
                        log.error("User not found with id {}", userId);
                        return new UserNotFoundException("User not found");
                    });

            Cart cart = cartRepository.findUserCartQuery(userId.toString())
                    .orElseGet(()->{
                        log.info("creating cart for user: {}",userId);
                        Cart newCart = new Cart();
                        newCart.setUser(user);
                        newCart.setCartItems(new ArrayList<>());
                        return cartRepository.save(newCart);
                    });

            List<CartItem> activeItems = cartItemsRepository.findActiveCartItems(cart.getId().toString());
            if(activeItems.isEmpty()){
                log.info("No active items found in cart with id: {}", cart.getId());
            }

            CartResponse cartResponse = convertToResponse(cart);

            return cartResponse;
        }
        catch (Exception ex){
            throw new CartServiceException("Something went wrong in cart service");
        }
    }

    @Override
    public CartResponse incrementQuantity(UUID userId, UUID productId) {
        try {
            userRepository.findByIdAndIsDeletedFalse(userId.toString())
                    .orElseThrow(()->{
                        log.error("User not found with id {}", userId);
                        return new UserNotFoundException("User not found");
                    });
            Product product = productRepository.findById(productId)
                    .orElseThrow(()->{
                        log.error("Product not found with id: {}",productId);
                        return new ProductNotFoundException("Product not found");
                    });
            if(product.isDeleted()){
                log.error("Product no longer exists with id: {}", productId);
                throw new ProductInActiveException("Product no longer exists");
            }
            Cart cart = cartRepository.findUserCartQuery(userId.toString())
                    .orElseThrow(()->{
                        log.error("Can't increment bcoz u don't have cart");
                        return new CartNotFoundException("You don't have cart");
                    });

            CartItem activeCartItem = cartItemsRepository.findActiveCartItem(cart.getId().toString(),product.getId().toString())
                    .orElseThrow(()->{
                        log.error("U don't have active product in cart with product id: {}",product.getId());
                        return new ProductNotFoundException("Product not found");
                    });
            log.info("cart id: {}  productId: {}", cart.getId(), product.getId());

            activeCartItem.setQuantity(activeCartItem.getQuantity()+1);

            cartItemsRepository.save(activeCartItem);

            CartResponse cartResponse = convertToResponse(cart);

            return cartResponse;
        }
        catch (UserNotFoundException | ProductNotFoundException | CartNotFoundException | ProductInActiveException ex){
            throw ex;
        }
        catch (Exception ex){
            log.error("Something went wrong in cart service");
            throw new CartServiceException("Something went wrong in cart service");
        }
    }

    @Override
    public CartResponse decrementQuantity(UUID userId, UUID productId) {
        try {
            userRepository.findByIdAndIsDeletedFalse(userId.toString())
                    .orElseThrow(()->{
                        log.error("User not found with id {}", userId);
                        return new UserNotFoundException("User not found");
                    });


            Product product = productRepository.findById(productId)
                    .orElseThrow(()->{
                        log.error("Product not found with id: {}",productId);
                        return new ProductNotFoundException("Product not found");
                    });
            if(product.isDeleted()){
                log.error("Product no longer exists with id: {}", productId);
                throw new ProductInActiveException("Product not longer exists");
            }
            Cart cart = cartRepository.findUserCartQuery(userId.toString())
                    .orElseThrow(()->{
                        log.error("Can't decrement bcoz u don't have cart");
                        return new CartNotFoundException("You don't have cart");
                    });
            CartItem activeCartItem = cartItemsRepository.findActiveCartItem(cart.getId().toString(),product.getId().toString())
                    .orElseThrow(()->{
                        log.error("U don't have active product in cart with product id: {}",product.getId());
                        return new ProductNotFoundException("Product not found");
                    });

                activeCartItem.setQuantity(activeCartItem.getQuantity()-1);
            cartItemsRepository.save(activeCartItem);

            CartResponse cartResponse = convertToResponse(cart);

            return cartResponse;
        }
        catch (UserNotFoundException | ProductNotFoundException | CartNotFoundException | ProductInActiveException ex){
            throw ex;
        }
        catch (Exception ex){
            log.error("Something went wrong in cart service");
            throw new CartServiceException("Something went wrong in cart service");
        }
    }

    @Override
    public CartResponse removeCartItem(UUID userId, UUID productId) {
        try {
            userRepository.findByIdAndIsDeletedFalse(userId.toString())
                    .orElseThrow(()->{
                        log.error("User not found with id {}", userId);
                        return new UserNotFoundException("User not found");
                    });
            Product product = productRepository.findById(productId)
                    .orElseThrow(()->{
                        log.error("Product not found with id: {}",productId);
                        return new ProductNotFoundException("Product not found");
                    });
            if(product.isDeleted()){
                log.error("Product no longer exists with id: {}", productId);
                throw new ProductInActiveException("Product not longer exists");
            }
            Cart cart = cartRepository.findUserCartQuery(userId.toString())
                    .orElseThrow(()->{
                        log.error("Can't decrement bcoz u don't have cart");
                        return new CartNotFoundException("You don't have cart");
                    });
            log.info("cart id: {} product id: {}", cart.getId().toString(), product.getId().toString());
            CartItem activeCartItem = cartItemsRepository.findActiveCartItem(cart.getId().toString(), product.getId().toString())
                    .orElseThrow(()->{
                        log.error("U don't have active product in cart with product 123 id: {}",product.getId());
                        return new ProductNotFoundException("Product not found");
                    });
            activeCartItem.setDeleted(true);
            cartItemsRepository.save(activeCartItem);

            CartResponse cartResponse = convertToResponse(cart);

            return cartResponse;
        }
        catch (UserNotFoundException | ProductNotFoundException | ProductInActiveException | CartNotFoundException ex){
            throw ex;
        }
        catch (Exception ex){
            log.error("Something went wrong in cart service");
            throw new CartServiceException("Something went wrong in cart service");
        }
    }

    private CartResponse convertToResponse(Cart cart) {

        CartResponse cartResponse = new CartResponse();
        BeanUtils.copyProperties(cart, cartResponse);
        cartResponse.setUserId(cart.getUser().getId());

        List<CartItemResponse> cartItemResponses = new ArrayList<>();
        List<CartItem> activeItems = cartItemsRepository.findActiveCartItems(cart.getId().toString());
        for(CartItem cartItem : activeItems){
            CartItemResponse cartItemResponse = new CartItemResponse();
            BeanUtils.copyProperties(cartItem, cartItemResponse);

            // product
            ProductResponse productResponse = new ProductResponse();
            BeanUtils.copyProperties(cartItem.getProduct(), productResponse);
            cartItemResponse.setProduct(productResponse);
            BigDecimal totalPrice = cartItem.getProduct().getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            cartItemResponse.setTotalPrice(totalPrice);
            cartItemResponses.add(cartItemResponse);

        }
        cartResponse.setCartItems(cartItemResponses);

        BigDecimal subTotal = cartItemResponses.stream()
                .map(CartItemResponse::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal finalTotal = subTotal;

        cartResponse.setSubTotal(subTotal);
//
        cartResponse.setFinalTotal(finalTotal);

        return cartResponse;
    }
}
