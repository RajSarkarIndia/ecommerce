package com.ecommerce.Payment.DTO;

import com.ecommerce.Payment.Enum.PaymentStatus;
import lombok.*;

@Getter
@Setter
public class PaymentDTO {
    private String paymentId;
    private String orderId;
    private PaymentStatus paymentStatus;
    private Double amount;


}