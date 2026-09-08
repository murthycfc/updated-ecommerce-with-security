package com.dependency.ecommerce.repo;

import com.dependency.ecommerce.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    @Query(value = "select * from users where id=:id and is_deleted=false", nativeQuery = true)
    Optional<User> findByIdAndIsDeletedFalse(@Param("id") String id);

    @Query(value = "select * from users where email= :email", nativeQuery = true)
    Optional<User> findEmail(String email);

    @Query(value = "select count(*)>0 from users where email= :email", nativeQuery = true)
    Long isEmailExisted(String email);

    @Query(value = "select * from users where is_deleted=false", nativeQuery = true)
    List<User> findAllActiveUsers();

    @Query(value = "select exists(select 1 from users where id =:userId and role='ADMIN')", nativeQuery = true)
    int checkIsAdmin(@Param("userId") String userId);
}
