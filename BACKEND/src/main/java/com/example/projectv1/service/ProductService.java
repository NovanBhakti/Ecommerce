package com.example.projectv1.service;

import com.example.projectv1.request.ProductRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public interface ProductService {
    ResponseEntity<?> addProduct(ProductRequest productRequest);
    ResponseEntity<?> showProductDetails(UUID id);
    ResponseEntity<?> showProductPicture(UUID id);
    ResponseEntity<?> uploadProductPicture(UUID id, MultipartFile file) throws IOException;
    ResponseEntity<?> editProduct(UUID id, ProductRequest productRequest);
    ResponseEntity<?> deleteProductPicture(UUID id);
    ResponseEntity<?> deleteProductByID(UUID id);
    ResponseEntity<?> showProductPictureById(UUID id);
}
