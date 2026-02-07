package com.ecommerce.product.DTO;
import lombok.*;

@Getter
@Setter
public class ProductResponse {
    private Integer productId;
    private String title;
    private Integer price;
    private Integer stock;//update the stock too
    private String status;


}

