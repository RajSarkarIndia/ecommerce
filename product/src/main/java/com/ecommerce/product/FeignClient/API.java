package com.ecommerce.product.FeignClient;


import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name="",url=lb://order)
public class API {
}
