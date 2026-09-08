package com.dependency.ecommerce.repo;

import com.dependency.ecommerce.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    @Query(value = "select * from products where id =:id", nativeQuery = true)
    Optional<Product> findById(@Param("id") String id);

    @Query(value = "select * from products where is_deleted = false",
            countQuery = "select count(*) from products where is_deleted = false",
            nativeQuery = true)
    Page<Product> findByIsDeletedFalse(Pageable pageable);

    @Query(value = "select * from products where id = :id and is_deleted = false", nativeQuery = true)
    Optional<Product> findByIdAndIsDeletedFalseQuery(@Param("id") String id);

    @Query(value = "select * from products where is_deleted=false and lower(title) like lower(concat('%',:name,'%'))",
            countQuery = "select count(*) from products where is_deleted=false and lower(title) like lower(concat('%',:name,'%'))",
            nativeQuery = true)
    Page<Product> searchByName(@Param("name") String name, Pageable pageable);

    @Query(value = "select * from products where is_deleted=false and binary category=:category",
            countQuery = "select count(*) from products where is_deleted=false and binary category=:category",
            nativeQuery = true)
    Page<Product> filterByCategory(@Param("category") String category, Pageable pageable);

    @Query(value = "select * from products order by price asc", nativeQuery = true)
    List<Product> filterByLowToHigh();

    @Query(value = "select * from products order by price desc", nativeQuery = true)
    List<Product> filterByHighToLow();


    @Query(value = "select * from products where id =:id", nativeQuery = true)
    Optional<Product> findByProductId(String id);

    @Query(value = """
                select * from products where is_deleted=false and 
                                             (:name='' or lower(title) like lower(concat('%',:name,'%'))) and 
                                            (:category=lower('All') or lower(category)=lower(:category))""",
            countQuery = """
                    select count(*) from products where is_deleted=false and
                    (select :name='' or lower(title) like lower(concat('%',:name,'%'))) and
                    (:category=lower('All') or lower(category)=lower(:category))""",
            nativeQuery = true)
    Page<Product> filterProducts (@Param("name") String name, @Param("category") String category, Pageable pageable);
}