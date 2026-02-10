package com.ecommerce.order.DTO;


import com.ecommerce.order.Enum.PaymentStatus;
import lombok.*;

@Getter
@Setter
public class PaymentDTO {
    private String paymentId;
    private Integer orderId;
    private PaymentStatus paymentStatus;
    private Double amount;


}
