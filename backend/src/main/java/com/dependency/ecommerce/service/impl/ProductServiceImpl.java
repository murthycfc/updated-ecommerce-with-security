package com.dependency.ecommerce.service.impl;

import com.dependency.ecommerce.dto.ProductRequest;
import com.dependency.ecommerce.dto.ProductResponse;
import com.dependency.ecommerce.exception.ProductNotFoundException;
import com.dependency.ecommerce.exception.ProductServiceException;
import com.dependency.ecommerce.model.Product;
import com.dependency.ecommerce.repo.ProductRepository;
import com.dependency.ecommerce.service.api.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepo;

    ProductServiceImpl(ProductRepository productRepo){
        this.productRepo = productRepo;
    }

    @Override
    public ProductResponse addProduct(ProductRequest request) {
        try {
            Product product = new Product();

            BeanUtils.copyProperties(request, product);

            Product savedProduct = productRepo.save(product);

            ProductResponse response = new ProductResponse();
            BeanUtils.copyProperties(savedProduct, response);
            return response;
        }
        catch (Exception ex){
            log.error("Unexpected error while adding product: {}", request.getTitle(),ex);
            throw new ProductServiceException("Failed to add Product");
        }
    }

    @Override
    public Page<ProductResponse> getAllProducts(int page, int size) {
        try {

            Pageable pageable = PageRequest.of(page, size);
            Page<Product> products = productRepo.findByIsDeletedFalse(pageable);

            return products.map(product -> {
                ProductResponse response = new ProductResponse();
                BeanUtils.copyProperties(product,response);
                return response;
            });
        }
        catch (Exception  ex){
            log.error("Unexpected error while fetching products", ex);
            throw new ProductServiceException("Failed to fetch products");
        }
    }

    @Override
    public ProductResponse getProductById(UUID id) {
        try {
            Product product = productRepo.findByProductId(id.toString())
                    .orElseThrow(()->{
                            log.error("Product not found with ID: {}",id);
                            return new ProductNotFoundException("Product not found");
                    });

            if(product.isDeleted()){
                log.error("Product no longer exists with ID: {}", id);
                throw new ProductNotFoundException("Product no longer exists");
            }
            ProductResponse response = new ProductResponse();
            BeanUtils.copyProperties(product, response);

            return response;
        }
        catch (ProductNotFoundException ex){
            throw ex;
        }
        catch (Exception  ex){
            log.error("Unexpected error while fetching  product: {}",id, ex);
            throw new ProductServiceException("Failed to fetch product");
        }
    }


    @Override
    public Page<ProductResponse> getFilteredProducts(String name, String category, String sort, int page, int size) {
        try {
            Pageable pageable;
            if(sort==null || sort.isBlank()){
                pageable = PageRequest.of(page, size);
            }
            else{
                Sort.Direction direction = sort.equalsIgnoreCase("desc")?
                        Sort.Direction.DESC : Sort.Direction.ASC;
                pageable = PageRequest.of(page, size, Sort.by(direction, "price"));
            }
             Page<Product> products = productRepo.filterProducts(name, category, pageable);
            return products.map(product -> {
                ProductResponse response = new ProductResponse();
                BeanUtils.copyProperties(product, response);
                return response;
            });
        }
        catch (Exception  ex){
            log.error("Unexpected error while fetching products");
            throw new ProductServiceException("Failed to fetch product");
        }
    }

    @Override
    public ProductResponse updateProduct(UUID id, ProductRequest request) {
        try {
            Product existingProduct = productRepo.findByProductId(id.toString())
                    .orElseThrow(()->{
                        log.error("Product not found for update. ID: {}", id);
                        return new ProductNotFoundException("Product not found. So can't update");
                    });

            existingProduct.setTitle(request.getTitle());
            existingProduct.setCategory(request.getCategory());
            existingProduct.setDescription(request.getDescription());
            existingProduct.setPrice(request.getPrice());
            Product updatedProduct = productRepo.save(existingProduct);

            ProductResponse response = new ProductResponse();
            BeanUtils.copyProperties(updatedProduct, response);

            return response;
        }
        catch (ProductNotFoundException ex){
            throw ex;
        }
        catch (Exception  ex){
            log.error("Unexpected error while updating  product: {}",id, ex);
            throw new ProductServiceException("Failed to update product");
        }
    }

    @Override
    public void deleteProduct(UUID id) {
        try {
            Product existingProduct = productRepo.findByProductId(id.toString())
                    .orElseThrow(()->{
                        log.error("Product not found for deletion. ID: {}",id);
                        return new ProductNotFoundException("Product not found.So can't delete");
                    });
            existingProduct.setDeleted(true);
            productRepo.save(existingProduct);
            log.info("Product soft deleted successfully. Product ID:{}",id);
        }
        catch (ProductNotFoundException ex){
            throw ex;
        }
        catch (Exception  ex){
            log.error("Unexpected error while deleting product: {}",id, ex);
            throw new ProductServiceException("Failed to delete product");
        }
    }
}
