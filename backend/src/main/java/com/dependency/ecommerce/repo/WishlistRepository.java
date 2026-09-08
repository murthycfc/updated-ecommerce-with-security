package com.dependency.ecommerce.repo;

import com.dependency.ecommerce.model.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WishlistRepository extends JpaRepository<Wishlist, UUID> {
    @Query(value = "select * from wishlist where user_id=:userId and product_id=:productId and is_deleted=false", nativeQuery = true)
    Optional<Wishlist> findActiveWishlistItem(String userId, String productId);

    @Query(value = "select * from wishlist where user_id=:userId and is_deleted=false", nativeQuery = true)
    List<Wishlist> findActiveWishlistItems(String userId);
}