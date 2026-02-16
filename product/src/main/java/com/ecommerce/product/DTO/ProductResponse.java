package com.ecommerce.product.DTO;

import com.ecommerce.product.Enum.ProductCategory;
import com.ecommerce.product.Enum.ProductStatus;
import com.ecommerce.product.entity.ProductImages;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ProductResponse {

    private Integer productId;

    private String sku;

    private String title;

    private String description;

    private Double price;

    private Integer stock;

    private ProductStatus status;

    private LocalDateTime createdAt;

    private List<ProductCategory> categories;

    private List<ProductImageDetails> images;
}
