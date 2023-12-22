package com.example.projectv1.controller;

import com.example.projectv1.request.ProductRequest;
import com.example.projectv1.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth/authenticated/")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class ProductController {
    private final ProductService productService;

    @GetMapping("/product/all")
    public ResponseEntity<?> productList(){
        return null;
    }

    @PostMapping("/product/add-product")
    public ResponseEntity<?> addProducts(@RequestBody ProductRequest productRequest){
        return productService.addProduct(productRequest);
    }

    @GetMapping("/product")
    public ResponseEntity<?> productDetails(@RequestParam UUID id){
        return productService.showProductDetails(id);
    }

    @GetMapping("/product/image-list")
    public ResponseEntity<?> showProductImages(@RequestParam UUID id){
        return productService.showProductPicture(id);
    }

    @PutMapping("/product")
    public ResponseEntity<?> editProduct(@RequestParam UUID id, @RequestBody ProductRequest productRequest){
        return productService.editProduct(id, productRequest);
    }

    @PostMapping ("/product")
    public ResponseEntity<?> uploadProductImage(@RequestParam("image") MultipartFile file, @RequestParam UUID id) throws IOException {
        return productService.uploadProductPicture(id, file);
    }

    @DeleteMapping("/product")
    public ResponseEntity<?> deleteProductImage(@RequestParam UUID id){
        return productService.deleteProductPicture(id);
    }

    @DeleteMapping("/product/delete-product")
    public ResponseEntity<?> deleteProduct(@RequestParam UUID id){
        return productService.deleteProductByID(id);
    }

    @GetMapping("/product/image")
    public ResponseEntity<?> showProductImage(@RequestParam UUID id){
        return productService.showProductPictureById(id);
    }
}
