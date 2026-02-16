package com.ecommerce.order.DTO;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemResponse {

    private Integer productId;
    private Double unitPrice;
    private Integer quantity;
}
