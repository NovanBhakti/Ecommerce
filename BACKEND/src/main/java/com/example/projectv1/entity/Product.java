package com.example.projectv1.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
@Data   //it will generate setter, getter, to string
@Builder    //build object
@NoArgsConstructor
@AllArgsConstructor //the constructor
@Entity
@Table(name = "_product")
public class Product {

    @OneToOne(mappedBy = "product", cascade = {CascadeType.DETACH, CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH})
    @JoinColumn(name = "product_picture_id")
    private ProductPicture productPicture;

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
