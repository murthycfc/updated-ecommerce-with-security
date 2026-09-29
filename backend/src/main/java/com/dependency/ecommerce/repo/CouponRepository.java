package com.dependency.ecommerce.repo;

import com.dependency.ecommerce.model.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, UUID> {

    @Query(value = "select count(*)>0 from coupons where name =:name", nativeQuery = true)
    boolean isCouponExisted(String name);

    @Query(value = "select * from coupons where id =:id and is_deleted=false and start_date<=now() and now()<=end_date", nativeQuery = true)
    Optional<Coupon> isValidCoupon(String id);

    @Query(value = "select * from coupons where is_deleted=false and start_date<=now() and now()<=end_date", nativeQuery = true)
    List<Coupon> fetchAllCoupons();

    @Query(value = "select * from coupons where id =:id", nativeQuery = true)
    Optional<Coupon> getCouponDetails(@Param("id") String id);
}
