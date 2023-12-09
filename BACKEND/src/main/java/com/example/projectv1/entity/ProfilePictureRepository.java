package com.example.projectv1.entity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProfilePictureRepository extends JpaRepository<ProfilePicture, UUID> {
}
