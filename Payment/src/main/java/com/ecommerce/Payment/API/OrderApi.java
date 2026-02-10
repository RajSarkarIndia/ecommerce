package com.ecommerce.Payment.API;


import com.ecommerce.Payment.DTO.PaymentDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "order")
public interface OrderApi {

    @PutMapping("/paymentReceived")
    void paymentStatus(@RequestBody PaymentDTO paymentInfo);
}

