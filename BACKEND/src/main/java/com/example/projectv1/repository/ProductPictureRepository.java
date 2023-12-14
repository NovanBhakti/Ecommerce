package com.example.projectv1.repository;

import com.example.projectv1.entity.ProductPicture;
import com.example.projectv1.entity.ProfilePicture;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductPictureRepository extends JpaRepository<ProductPicture, UUID> {
}
