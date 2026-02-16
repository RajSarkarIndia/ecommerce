package com.ecommerce.order.DTO;
import lombok.*;

@Getter
@Setter
public class ProductInfoForBuying {
    private Integer productId;
    private Integer quantity;
    private Integer addressId;
}
