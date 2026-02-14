package com.ecommerce.product.DTO;

import com.ecommerce.product.Enum.ProductStatus;

import java.util.List;

import lombok.*;


@Getter
@Setter
public class ProductInfo {

    private String sku;
    private String title;
    private String description;
    private Double price;
    private Integer stock;
    private ProductStatus status;
    private List<String> categories;


}
