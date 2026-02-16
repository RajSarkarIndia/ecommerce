package com.ecommerce.order.FeignClient;


import com.ecommerce.order.DTO.PaymentDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "PAYMENT")
public interface PaymentAPI {

    @PostMapping("/payment/create")
    String createSessionUrl(@RequestBody PaymentDTO paymentInfo);

}

