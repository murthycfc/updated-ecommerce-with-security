package com.dependency.ecommerce.repo;

import com.dependency.ecommerce.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CartItemsRepository extends JpaRepository<CartItem, UUID> {

    @Query(value="select * from cart_items where cart_id=:cart_id and product_id=:product_id and is_deleted=false", nativeQuery = true )
    Optional<CartItem> findActiveCartItem(String cart_id, String product_id);

    @Query(value = "select * from cart_items where cart_id=:cart_id and is_deleted=false", nativeQuery = true)
    List<CartItem> findActiveCartItems(String cart_id);
}
