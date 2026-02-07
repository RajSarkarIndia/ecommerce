package com.ecommerce.order.DTO;
import lombok.*;

@Getter
@Setter
public class ProductResponse {
    private Integer productId;
    private String title;
    private Double price;
    private Integer stock;//update the stock too
    private String status;


}
