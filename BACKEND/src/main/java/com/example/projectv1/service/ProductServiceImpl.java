package com.example.projectv1.service;

import com.example.projectv1.entity.Product;
import com.example.projectv1.entity.ProductPicture;
import com.example.projectv1.handler.ProductIdNotFoundException;
import com.example.projectv1.repository.ProductPictureRepository;
import com.example.projectv1.repository.ProductRepository;
import com.example.projectv1.request.ProductRequest;
import com.example.projectv1.response.GlobalResponse;
import com.example.projectv1.response.ProductResponse;
import com.example.projectv1.utils.ImageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{
    private final ProductRepository productRepository;
    private final ProductPictureRepository productPictureRepository;
    private final ImageHandlerService imageHandlerService;

    private Product getProductById(UUID id){
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isPresent()) {
            try {
                return productOptional.get();
            } catch (NullPointerException e){
                return productOptional.get();
            }
        } else {
            throw new ProductIdNotFoundException("Product with ID " + id + " not found");
        }
    }

    @Override
    public ResponseEntity<?> addProduct(ProductRequest productRequest) {
        if (productRequest.getQuantity() < 1){
            System.out.println(productRequest.getQuantity());
            return GlobalResponse.responseHandler("Product cannot be empty!", HttpStatus.OK, null);
        }
        if (productRequest.getPrice() < 0){
            return GlobalResponse.responseHandler("Product price cannot be minus!", HttpStatus.OK, null);
        }
        if (productRequest.getName().isBlank()){
            return GlobalResponse.responseHandler("Product name cannot be empty!", HttpStatus.OK, null);
        }
        if (productRequest.getCategory() == null){
            return GlobalResponse.responseHandler("Product must have a category", HttpStatus.OK, null);
        }

        var product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .quantity(productRequest.getQuantity())
                .category(productRequest.getCategory())
                .createdDate(LocalDateTime.now())
                .build();

        productRepository.save(product);

        return GlobalResponse.responseHandler("Successfully added product", HttpStatus.OK, product);
    }

    @Override
    public ResponseEntity<?> showProductDetails(UUID id) {
        try {
            Product product = getProductById(id);
            Object base64Image = imageHandlerService.rawImageBase64(product);
            List<String> base64Images = new ArrayList<>();
            if (base64Image instanceof String) {
                base64Images.add((String) base64Image);
            } else if (base64Image instanceof List) {
                base64Images.addAll((List<String>) base64Image);
            } else {
                throw new UnsupportedOperationException("Unsupported base64Image type: " + base64Image.getClass().getSimpleName());
            }
            return GlobalResponse
                    .responseHandler("Successfully retrieving product data", HttpStatus.OK, ProductResponse.builder()
                            .id(product.getId())
                            .name(product.getName())
                            .description(product.getDescription())
                            .price(product.getPrice())
                            .quantity(product.getQuantity())
                            .category(String.valueOf(product.getCategory()))
                            .productPictures(base64Images)
                            .build());
        } catch (Exception e) {
            return GlobalResponse.responseHandler(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    @Override
    public ResponseEntity<?> showProductPicture(UUID id) {
        Product product = getProductById(id);
        return imageHandlerService.showImage(product);
    }

    @Override
    public ResponseEntity<?> uploadProductPicture(UUID id, MultipartFile file) throws IOException {
        try {
            return imageHandlerService.uploadImage(file, getProductById(id));
        } catch (ProductIdNotFoundException | NullPointerException e){
            return GlobalResponse.responseHandler(e.getMessage(), HttpStatus.NOT_FOUND, null);
        }
    }

    @Override
    public ResponseEntity<?> editProduct(UUID id, ProductRequest productRequest) {
        StringBuilder updatedFields = new StringBuilder("Updated fields: ");

        Product product = getProductById(id);
        if (!(productRequest.getName().isBlank()) && !productRequest.getName().equals(product.getName())){
            product.setName(productRequest.getName());
            updatedFields.append("name, ");
        }
        if (!productRequest.getDescription().equals(product.getDescription())
                && productRequest.getDescription().length() >= 20){
            product.setDescription(productRequest.getDescription());
            updatedFields.append("description, ");
        }
        if ((productRequest.getQuantity() + product.getQuantity()) >= 0) {
            product.setQuantity(productRequest.getQuantity() + product.getQuantity());
            updatedFields.append("quantity, ");
        }
        if (productRequest.getCategory() != null && !productRequest.getCategory().equals(product.getCategory())){
            product.setCategory(productRequest.getCategory());
            updatedFields.append("category, ");
        }
        if (productRequest.getPrice() >= 0 && productRequest.getPrice() != product.getPrice()){
            product.setPrice(productRequest.getPrice());
            updatedFields.append("price, ");
        }

        productRepository.save(product);

        String responseMessage;
        if (updatedFields.length() > "Updated fields: ".length()) {
            responseMessage = updatedFields.substring(0, updatedFields.length() - 2);
        } else {
            responseMessage = "No fields were updated";
        }

        return GlobalResponse
                .responseHandler(responseMessage, HttpStatus.OK, ProductResponse.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .description(product.getDescription())
                        .price(product.getPrice())
                        .quantity(product.getQuantity())
                        .category(String.valueOf(product.getCategory()))
                        .build());
    }

    @Override
    public ResponseEntity<?> deleteProductPicture(UUID id) {
        return imageHandlerService.deleteImage(getProductById(id));
    }

    @Override
    public ResponseEntity<?> deleteProductByID(UUID id){
        deleteProductPicture(id);
        productRepository.deleteById(id);
        return GlobalResponse.responseHandler("Product with id : " + id + " successfully deleted", HttpStatus.OK, null);
    }

    @Override
    public ResponseEntity<?> showProductPictureById(UUID id) {
        return imageHandlerService.showProductImageById(id);
    }


}
