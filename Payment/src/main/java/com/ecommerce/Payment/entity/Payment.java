package com.ecommerce.Payment.entity;


import com.ecommerce.Payment.Enum.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name="payment")
public class Payment {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column
    private Integer paymentId;
    @Column
    private String orderId;
    @Column
    private String stripeSessionId;
    @Column
    private PaymentStatus paymentStatus;
    @Column
    private Double amount;




}
