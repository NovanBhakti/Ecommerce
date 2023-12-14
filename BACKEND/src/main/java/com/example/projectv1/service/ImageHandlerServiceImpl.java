package com.example.projectv1.service;

import com.example.projectv1.entity.Product;
import com.example.projectv1.entity.ProductPicture;
import com.example.projectv1.entity.ProfilePicture;
import com.example.projectv1.entity.User;
import com.example.projectv1.repository.ProductPictureRepository;
import com.example.projectv1.repository.ProductRepository;
import com.example.projectv1.repository.ProfilePictureRepository;
import com.example.projectv1.repository.UserRepository;
import com.example.projectv1.response.GlobalResponse;
import com.example.projectv1.response.ProfileImageResponse;
import com.example.projectv1.utils.ImageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Transactional
@RequiredArgsConstructor
public class ImageHandlerServiceImpl<T> implements ImageHandlerService<T> {
    private final ProfilePictureRepository profilePictureRepository;
    private final ProductPictureRepository productPictureRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    public ResponseEntity<?> uploadImage(MultipartFile file, T entity) throws IOException {
        try {
            if (file.isEmpty()) {
                return GlobalResponse
                        .responseHandler("The file is empty", HttpStatus.BAD_REQUEST,
                                ProfileImageResponse
                                        .builder()
                                        .build());
            } else if (!ImageUtils.isImage(file)) {
                return GlobalResponse
                        .responseHandler("Wrong file extension! Only upload PNG, JPG, and JPEG file extensions",
                                HttpStatus.BAD_REQUEST,
                                ProfileImageResponse
                                        .builder()
                                        .build());
            } else {
                if (entity instanceof User user) {
                    if (user.getProfilePicture() != null) {
                        user.getProfilePicture().setProfilePicture(ImageUtils.imageCompressor(file.getBytes()));
                        userRepository.save(user);
                        return GlobalResponse.responseHandler("Image updated", HttpStatus.OK, ProfileImageResponse.builder().file(user.getProfilePicture().getId()).build());
                    }
                    else {
                        ProfilePicture profilePicture = new ProfilePicture();
                        profilePicture.setProfilePicture(ImageUtils.imageCompressor(file.getBytes()));
                        profilePicture.setUser(user);
                        user.setProfilePicture(profilePicture);
                        userRepository.save(user);
                        return GlobalResponse.responseHandler("Image uploaded", HttpStatus.OK, ProfileImageResponse.builder().file(profilePicture.getId()).build());
                    }
                } else if (entity instanceof Product product) {
                    if (product.getProductPicture() != null) {
                        product.getProductPicture().setProductPicture(ImageUtils.imageCompressor(file.getBytes()));
                        productRepository.save(product);
                        return GlobalResponse.responseHandler("Image updated", HttpStatus.OK, ProfileImageResponse.builder().file(product.getProductPicture().getId()).build());
                    } else {
                        ProductPicture productPicture = new ProductPicture();
                        productPicture.setProductPicture(ImageUtils.imageCompressor(file.getBytes()));
                        productPicture.setProduct(product);
                        product.setProductPicture(productPicture);
                        productRepository.save(product);
                        return GlobalResponse.responseHandler("Image uploaded", HttpStatus.OK, ProfileImageResponse.builder().file(productPicture.getId()).build());
                    }
                } else {
                    return GlobalResponse.responseHandler("Wrong entity", HttpStatus.BAD_REQUEST, null);
                }
            }
        } catch (UsernameNotFoundException e) {
            return GlobalResponse.responseHandler(e.getMessage(), HttpStatus.UNAUTHORIZED, ProfileImageResponse.builder().build());
        }
    }

    @Override
    public ResponseEntity<?> showImage(T entity) {
        try {
            if(entity instanceof User user){
                ProfilePicture profilePicture = user.getProfilePicture();
                byte[] images = ImageUtils.imageDecompressor(profilePicture.getProfilePicture());
                return GlobalResponse.responseHandler("Successfully retrieving image", HttpStatus.OK, images);
            } else if (entity instanceof Product product) {
                ProductPicture productPicture = product.getProductPicture();
                byte[] images = ImageUtils.imageDecompressor(productPicture.getProductPicture());
                return GlobalResponse.responseHandler("Successfully retrieving image", HttpStatus.OK, images);
            } else {
                return GlobalResponse.responseHandler("Wrong entity", HttpStatus.BAD_REQUEST, null);
            }
        } catch (NullPointerException e) {
            return GlobalResponse.responseHandler("Profile picture is empty", HttpStatus.OK, null);
        } catch (Exception e) {
            return GlobalResponse.responseHandler(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    @Override
    public ResponseEntity<?> deleteImage(T entity) {
        try {
            if (entity instanceof User user){
                if (user.getProfilePicture() != null) {
                    ProfilePicture profilePicture = user.getProfilePicture();
                    profilePictureRepository.delete(profilePicture);
                    user.setProfilePicture(null);
                    userRepository.save(user);
                    return GlobalResponse.responseHandler("Image deleted", HttpStatus.OK, ProfileImageResponse.builder().build());
                } else {
                    return GlobalResponse.responseHandler("There's no image", HttpStatus.OK, ProfileImageResponse.builder().build());
                }
            } else if (entity instanceof Product product) {
                if (product.getProductPicture() != null) {
                    ProductPicture productPicture = product.getProductPicture();
                    productPictureRepository.delete(productPicture);
                    productPicture.setProductPicture(null);
                    productRepository.save(product);
                    return GlobalResponse.responseHandler("Image deleted", HttpStatus.OK, ProfileImageResponse.builder().build());
                } else {
                    return GlobalResponse.responseHandler("There's no image", HttpStatus.OK, ProfileImageResponse.builder().build());
                }
            } else {
                return GlobalResponse.responseHandler("Wrong entity", HttpStatus.BAD_REQUEST, null);
            }
        } catch (Exception e){
            return GlobalResponse.responseHandler(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }
}
