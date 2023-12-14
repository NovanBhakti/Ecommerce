package com.example.projectv1.service;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageHandlerService<T> {

    ResponseEntity<?> uploadImage(MultipartFile file, T entity) throws IOException;

    ResponseEntity<?> showImage(T entity);

    ResponseEntity<?> deleteImage(T entity);
}
