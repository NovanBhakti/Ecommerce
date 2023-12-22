package com.example.projectv1.service;

import com.example.projectv1.entity.ProductPicture;
import com.example.projectv1.entity.ProfilePicture;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

public interface ImageHandlerService<T> {

    ResponseEntity<?> uploadImage(MultipartFile file, T entity) throws IOException;
    ResponseEntity<?> showImage(T entity);
    Object rawImageBase64(T entity) throws Exception;
    ResponseEntity<?> deleteImage(T entity);
    Optional<ProductPicture> getProductPictureById(UUID id);
    ResponseEntity<?> showProductImageById(UUID id);
}
