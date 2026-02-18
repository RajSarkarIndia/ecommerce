package com.ecommerce.order.FeignClient;

import com.ecommerce.order.DTO.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "PRODUCT")
public interface ProductAPI {

    @PutMapping("/product/buy/{productId}/{quantity}")
    ProductResponse fetchProductById(
            @PathVariable("productId") Integer productId,
            @PathVariable("quantity") Integer quantity
    );
}

