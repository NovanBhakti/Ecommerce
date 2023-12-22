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
import com.example.projectv1.response.PictureResponse;
import com.example.projectv1.response.ProductResponse;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
                    } else {
                        ProfilePicture profilePicture = new ProfilePicture();
                        profilePicture.setProfilePicture(ImageUtils.imageCompressor(file.getBytes()));
                        profilePicture.setUser(user);
                        user.setProfilePicture(profilePicture);
                        userRepository.save(user);
                        return GlobalResponse.responseHandler("Image uploaded", HttpStatus.OK, ProfileImageResponse.builder().file(profilePicture.getId()).build());
                    }
                } else if (entity instanceof Product product) {
                        ProductPicture productPicture = new ProductPicture();
                        productPicture.setProductPicture(ImageUtils.imageCompressor(file.getBytes()));
                        productPicture.setProduct(product);
                        product.getProductPictures().add(productPicture);
                        productRepository.save(product);
                        return GlobalResponse.responseHandler("Image uploaded", HttpStatus.OK, ProfileImageResponse.builder().file(productPicture.getId()).build());
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
        if(rawImageBase64(entity) != null) {
            if (entity instanceof Product product) try {
                Object base64Image = rawImageBase64(entity);
                List<String> base64Images = new ArrayList<>();
                if (base64Image instanceof String) {
                    base64Images.add((String) base64Image);
                } else if (base64Image instanceof List) {
                    base64Images.addAll((List<String>) base64Image);
                } else {
                    throw new UnsupportedOperationException("Unsupported base64Image type: " + base64Image.getClass().getSimpleName());
                }
            } catch (Exception e) {
                GlobalResponse.responseHandler(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null);
            }
            return GlobalResponse.responseHandler("Successfully retrieving image", HttpStatus.OK, rawImageBase64(entity));
        } else {
            return GlobalResponse.responseHandler("The image is empty", HttpStatus.OK, null);
        }
    }

    @Override
    public Object rawImageBase64(T entity) {
        try {
            if (entity instanceof User user) {
                ProfilePicture profilePicture = user.getProfilePicture();
                return ImageUtils.convertToBase64(profilePicture.getProfilePicture());
            } else if (entity instanceof Product product) {
                List<ProductPicture> productPictures = product.getProductPictures();
                List<String> rawImageList = new ArrayList<>();

                for (ProductPicture productPicture : productPictures) {
                    byte[] pictureBytes = productPicture.getProductPicture();
                    String base64Image = ImageUtils.convertToBase64(pictureBytes);
                    rawImageList.add(base64Image);
                }
                return rawImageList;
            } else {
                throw new IllegalArgumentException("Wrong entity type!");
            }
        } catch (NullPointerException e) {
            return null;
        }
    }

    @Override
    public ResponseEntity<?> deleteImage(T entity) {
        try {
            if (entity instanceof User user) {
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
                if (product.getProductPictures() != null && !product.getProductPictures().isEmpty()) {
                    List<ProductPicture> productPictures = new ArrayList<>(product.getProductPictures());
                    for (ProductPicture productPicture : productPictures) {
                        productPicture.setProduct(null);
                        productPictureRepository.delete(productPicture);
                    }
                    product.getProductPictures().clear();

                    productRepository.save(product);

                    return GlobalResponse.responseHandler("Images deleted", HttpStatus.OK, null);
                } else {
                    return GlobalResponse.responseHandler("There are no images to delete", HttpStatus.OK, null);
                }
            } else {
                return GlobalResponse.responseHandler("Wrong entity", HttpStatus.BAD_REQUEST, null);
            }
        } catch (Exception e) {
            return GlobalResponse.responseHandler(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    @Override
    public Optional<ProductPicture> getProductPictureById(UUID id) {
        return productPictureRepository.findById(id);
    }

    @Override
    public ResponseEntity<?> showProductImageById(UUID id) {
        try {
            Optional<ProductPicture> productPictureOptional = getProductPictureById(id);
            if (productPictureOptional.isPresent()) {
                ProductPicture productPicture = productPictureOptional.get();
                String image = ImageUtils.convertToBase64(productPicture.getProductPicture());
                return GlobalResponse.responseHandler("Successfully retrieving product image", HttpStatus.OK, PictureResponse.builder().pictureId(productPicture.getId()).entityId(productPicture.getProduct().getId()).file(image).build());
            } else {
                return GlobalResponse.responseHandler("Image with id: " + id + " not found!", HttpStatus.NOT_FOUND, null);
            }
        } catch (NullPointerException e){
            return GlobalResponse.responseHandler("Image with id: " + id + " not found!", HttpStatus.BAD_REQUEST, null);
        }
    }
}
