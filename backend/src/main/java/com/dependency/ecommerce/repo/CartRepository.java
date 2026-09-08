package com.dependency.ecommerce.repo;

import com.dependency.ecommerce.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface CartRepository extends JpaRepository<Cart, UUID> {
    @Query(value = "select * from carts where user_id=:user", nativeQuery = true)
    Optional<Cart> findUserCartQuery(String user);

    @Query(value = "select * from carts where id=:id", nativeQuery = true)
    Optional<Cart> findCartById(String id);
}
