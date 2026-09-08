package com.dependency.ecommerce.repo;

import com.dependency.ecommerce.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    @Query(value = "select * from orders where user_id=:userId" , nativeQuery = true)
    List<Order> findByUserId(String userId);

    @Query(value = "select * from orders order by order_number desc limit 1", nativeQuery = true)
    Optional<Order> getLatestOrderNumber();

    @Query(value = "select o.* from orders o join users u on o.user_id=u.id where o.id=:orderId and u.id=:requestingUserId", nativeQuery = true)
    Optional<Order> findOrderByIdAndUserIdQuery(@Param("requestingUserId") String requestingUserId, @Param("orderId") String orderId);

    @Query(value= """
        select o.* from orders o join users u on o.user_id=u.id where u.is_deleted=false
    """, nativeQuery = true)
    List<Order> findAllOrders();

    @Query(value = "select * from orders where id=:orderId", nativeQuery = true)
    Optional<Order> findByOrderId(@Param("orderId") String orderId);
}
