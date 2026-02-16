package com.ecommerce.product.entity;

import com.ecommerce.product.Enum.ProductCategory;
import com.ecommerce.product.Enum.ProductStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer productId;

    @Column(name="userId",nullable=false)
    private Integer userId;

    @Column(nullable = false, length = 40)
    private String sku;

    @Column(name = "product_title", nullable = false, length = 100)
    private String title;

    @Column(name = "title_vector_embeddings", columnDefinition = "TEXT")
    private String vectorEmbedding;

    @Column(name = "product_description", nullable = false, length = 3000)
    private String description;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Integer stock;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProductStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt= LocalDateTime.now();

    @ElementCollection
    @CollectionTable(name = "product_category", joinColumns = @JoinColumn(name = "product_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private List<ProductCategory> categories;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImages> productImages;


}
