package com.dependency.ecommerce.facade.impl;

import com.dependency.ecommerce.dto.ProductRequest;
import com.dependency.ecommerce.dto.ProductResponse;
import com.dependency.ecommerce.exception.AccessDeniedException;
import com.dependency.ecommerce.exception.UserNotFoundException;
import com.dependency.ecommerce.facade.api.ProductFacade;
import com.dependency.ecommerce.model.Product;
import com.dependency.ecommerce.model.User;
import com.dependency.ecommerce.repo.ProductRepository;
import com.dependency.ecommerce.repo.UserRepository;
import com.dependency.ecommerce.service.api.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class ProductFacadeImpl implements ProductFacade {

    private final ProductRepository productRepo;
    private final UserRepository userRepo;
    private final ProductService productService;

    ProductFacadeImpl(ProductRepository productRepo, UserRepository userRepo, ProductService productService){
        this.productRepo = productRepo;
        this.userRepo = userRepo;
        this.productService = productService;
    }

    @Override
    public ProductResponse addProduct(UUID userId, ProductRequest request) {
        validateAdmin(userId);
        return productService.addProduct(request);
    }

    @Override
    public Page<ProductResponse> getAllProducts(int page, int size) {
        return productService.getAllProducts(page, size);
    }

    @Override
    public ProductResponse getProductById(UUID id) {

        return productService.getProductById(id);
    }

    @Override
    public Page<ProductResponse> getFilteredProducts(String name, String category, String sort, int page, int size) {
        return productService.getFilteredProducts(name,category,sort, page, size);
    }

    @Override
    public ProductResponse updateProduct(UUID userId, UUID id, ProductRequest request) {
        validateAdmin(userId);
        return productService.updateProduct(id, request);
    }

    @Override
    public void deleteProduct(UUID userId, UUID id) {
        validateAdmin(userId);
        productService.deleteProduct(id);
    }

    public void validateAdmin(UUID userId){
        User user = userRepo.findById(userId)
                        .orElseThrow(()->
                                new UserNotFoundException("User not Found with id: "+userId)
                        );
        if(!"ADMIN".equalsIgnoreCase(user.getRole())){
            throw new AccessDeniedException(
                    "Access denied. Only admin can perform this operation.");
        }
    }
}
