package com.ecommerce.order.FeignClient;


import com.ecommerce.order.DTO.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "product", url = "http://localhost:8080")
public interface ProductAPI {

    @PostMapping("/fetchProductById/{productId}/{quantity}")
    public ProductResponse fetchProductById(@PathVariable("productId")Integer productId, @PathVariable("quantity") Integer quantity);






    }
