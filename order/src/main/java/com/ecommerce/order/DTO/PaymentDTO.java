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

    public PaymentDTO(String paymentId, Integer orderId, PaymentStatus paymentStatus, Double amount){
        this.paymentId=paymentId;
        this.orderId=orderId;
        this.paymentStatus=paymentStatus;
        this.amount=amount;
    }

}
