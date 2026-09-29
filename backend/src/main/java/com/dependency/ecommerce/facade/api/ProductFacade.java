package com.dependency.ecommerce.facade.api;

import com.dependency.ecommerce.dto.ProductRequest;
import com.dependency.ecommerce.dto.ProductResponse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface ProductFacade {
    ProductResponse addProduct(UUID userId, ProductRequest request);
    List<ProductResponse> getAllProducts();
    ProductResponse getProductById(UUID id);
    Page<ProductResponse> getFilteredProducts(String name, String category, String sort, int page, int size);
    ProductResponse updateProduct(UUID userId, UUID id, ProductRequest request);
    void deleteProduct(UUID userId, UUID id);
}
