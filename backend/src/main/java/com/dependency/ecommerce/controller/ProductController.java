package com.dependency.ecommerce.controller;

import com.dependency.ecommerce.dto.ProductRequest;
import com.dependency.ecommerce.dto.ProductResponse;
import com.dependency.ecommerce.facade.api.ProductFacade;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductFacade productFacade;
    ProductController(ProductFacade productFacade){
        this.productFacade = productFacade;
    }

    @PostMapping("/add")
    public ResponseEntity<ProductResponse> addProduct(@RequestParam UUID userId,
                                                      @RequestBody ProductRequest request){
        return ResponseEntity.ok(productFacade.addProduct(userId, request));
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts(){

        return ResponseEntity.ok(productFacade.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable UUID id){
        return ResponseEntity.ok(productFacade.getProductById(id));
    }


    @GetMapping("filterProducts")
    public ResponseEntity<Page<ProductResponse>> filterProducts(@RequestParam String name,
                                                                @RequestParam String category,
                                                                @RequestParam String sort,
                                                                @RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.ok(productFacade.getFilteredProducts(name, category, sort, page, size));
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@RequestParam UUID userId,
                                                 @PathVariable UUID id,
                                                 @RequestBody ProductRequest request){
        return ResponseEntity.ok(productFacade.updateProduct(userId, id, request));
    }

    @PutMapping("/delete/{id}")
    public ResponseEntity<Void> deleteProduct(@RequestParam UUID userId,
                                              @PathVariable UUID id){
        productFacade.deleteProduct(userId, id);
        return ResponseEntity.noContent().build();
    }
}
