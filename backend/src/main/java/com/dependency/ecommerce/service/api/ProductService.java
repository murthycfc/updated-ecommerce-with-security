package com.dependency.ecommerce.service.api;

import com.dependency.ecommerce.dto.ProductRequest;
import com.dependency.ecommerce.dto.ProductResponse;
import com.dependency.ecommerce.model.Product;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    ProductResponse addProduct(ProductRequest request);
    List<ProductResponse> getAllProducts();
    ProductResponse getProductById(UUID id);
    Page<ProductResponse> getFilteredProducts(String name, String category, String sort, int page, int size);
    ProductResponse updateProduct(UUID id, ProductRequest request);
    void deleteProduct(UUID id);

}
