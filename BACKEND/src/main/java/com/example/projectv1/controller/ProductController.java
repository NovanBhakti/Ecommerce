package com.example.projectv1.controller;

import com.example.projectv1.request.ProductRequest;
import com.example.projectv1.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth/authenticated/")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class ProductController {
    private final ProductService productService;

    @GetMapping("/product")
    public ResponseEntity<?> productList(){
        return null;
    }

    @PostMapping("/product/add-product")
    public ResponseEntity<?> addProducts(@RequestBody ProductRequest productRequest){
        return productService.addProduct(productRequest);
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<?> productDetails(@PathVariable UUID id){
        return productService.showProductDetails(id);
    }
}
