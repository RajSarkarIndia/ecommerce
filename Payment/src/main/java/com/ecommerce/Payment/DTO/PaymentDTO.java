package com.ecommerce.Payment.DTO;

import lombok.*;

@Getter
@Setter
public class PaymentDTO {
    private Integer paymentId;
    private String orderId;
    private String stripeSessionId;
    private String paymentStatus;
    private Double amount;


}