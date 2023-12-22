package com.example.projectv1.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
@Data   //it will generate setter, getter, to string
@Builder    //build object
@NoArgsConstructor
@AllArgsConstructor //the constructor
@Entity
@Table(name = "_product")
public class Product {

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductPicture> productPictures = new ArrayList<>();

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    private String description;
    private double price;
    private int quantity;
    private LocalDateTime createdDate;
    @Enumerated(EnumType.STRING)
    private ProductCategory category;
}
