package com.example.projectv1.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.util.UUID;

@Data   //it will generate setter, getter, to string
@Builder    //build object
@NoArgsConstructor
@AllArgsConstructor //the constructor
@Entity
@Table(name = "_product_picture")
public class ProductPicture {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Lob
    @JdbcTypeCode(Types.LONGVARBINARY)
    private byte[] productPicture;

}
