package com.ecommerce.Payment.API;


import com.ecommerce.Payment.DTO.PaymentDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "order")
public interface OrderApi {

    @PutMapping("/order/paymentReceived")
    void paymentStatus(
            @RequestHeader("PaymentKey") String key,
            @RequestBody PaymentDTO paymentInfo
    );
}

