package com.example.projectv1.repository;

import com.example.projectv1.entity.User;
import com.example.projectv1.entity.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserAddressRepository extends JpaRepository<UserAddress, Integer> {
    List<UserAddress> findUserAddressesByUser(User user);
}
