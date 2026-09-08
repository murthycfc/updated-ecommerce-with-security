package com.dependency.ecommerce.service.impl;

import com.dependency.ecommerce.exception.ProductNotFoundException;
import com.dependency.ecommerce.exception.UserNotFoundException;
import com.dependency.ecommerce.exception.WishlistServiceException;
import com.dependency.ecommerce.model.Product;
import com.dependency.ecommerce.model.User;
import com.dependency.ecommerce.model.Wishlist;
import com.dependency.ecommerce.repo.CartRepository;
import com.dependency.ecommerce.repo.ProductRepository;
import com.dependency.ecommerce.repo.UserRepository;
import com.dependency.ecommerce.repo.WishlistRepository;
import com.dependency.ecommerce.service.api.WishlistService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Slf4j
@Service
public class WishlistServiceImpl implements WishlistService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final WishlistRepository wishlistRepository;

    public WishlistServiceImpl(
            UserRepository userRepository,
            ProductRepository productRepository,
            CartRepository cartRepository,
            WishlistRepository wishlistRepository){
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.wishlistRepository = wishlistRepository;
    }

    @Override
    public String addProduct(UUID userId, UUID productId) {
        try {
            User user = userRepository.findByIdAndIsDeletedFalse(userId.toString())
                    .orElseThrow(()->{
                        log.error("User not found with id: {}. So can't add product to wishlist", userId);
                        return new UserNotFoundException("User not found");
                    });

            Product product = productRepository.findByIdAndIsDeletedFalseQuery(productId.toString())
                    .orElseThrow(()->{
                        log.error("Product not found with id: {}",productId);
                        return new ProductNotFoundException("Product not found");
                    });
            Optional<Wishlist> activeItem = wishlistRepository.findActiveWishlistItem(userId.toString(),productId.toString());
            if(activeItem.isPresent()){
                return "Product already exists";
            }
            else{
                Wishlist wishlist = new Wishlist();
                wishlist.setUser(user);
                wishlist.setProduct(product);
                wishlist.setDeleted(false);
                wishlistRepository.save(wishlist);
                return "Product added to wishlist";
            }
        }
        catch (UserNotFoundException | ProductNotFoundException ex){
            throw ex;
        }
        catch (Exception ex){
            throw new WishlistServiceException("Something went wrong in wishlist service");
        }
    }

    @Override
    public List<Wishlist> getWishlist(UUID userId) {
        try {
            userRepository.findByIdAndIsDeletedFalse(userId.toString())
                    .orElseThrow(()->{
                        log.error("User not found with id: {}. so can't fetch wishlist", userId);
                        return new UserNotFoundException("User not found");
                    });
            List<Wishlist> wishlist = wishlistRepository.findActiveWishlistItems(userId.toString());
            return wishlist;
        }
        catch (UserNotFoundException ex){
            throw ex;
        }
    }

    @Transactional
    @Override
    public String removeProduct(UUID userId, UUID productId) {
        try {
            User user = userRepository.findByIdAndIsDeletedFalse(userId.toString())
                    .orElseThrow(() -> {
                        log.error("User not found with id: {} So can't remove product from wishlist", userId);
                        return new UserNotFoundException("User not found");
                    });
            log.info("searching for product id: {}", productId);
            Optional<Product> result = productRepository.findByIdAndIsDeletedFalseQuery(productId.toString());
            log.info("repo result: {}", result.isPresent());

            Product product = result.orElseThrow(() -> {
                        log.error("Product no longer exists with id: {}", productId);
                        return new ProductNotFoundException("Product no longer exists");
                    });

            Optional<Wishlist> activeItem = wishlistRepository.findActiveWishlistItem(userId.toString(), productId.toString());
            if (activeItem.isPresent()) {
                activeItem.get().setDeleted(true);
                return "Product removed from wishlist";
            } else {
                log.error("Product is not present in wishlist with product id: {}", productId);
                throw new ProductNotFoundException("Product not found in the wishlist");
            }
        }
        catch (UserNotFoundException | ProductNotFoundException ex){
            throw ex;
        }
    }
}
