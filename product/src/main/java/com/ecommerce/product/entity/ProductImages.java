package com.ecommerce.product.entity;


import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name="ProductImages")
public class ProductImages {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    Integer imageId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;


    @Column(name="ObjectName")
    String ObjectName;

    @Column(name="alt",length=40)
    String alt;

}
