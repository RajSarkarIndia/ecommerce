package com.ecommerce.Payment.entity;


import com.ecommerce.Payment.Enum.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    @Column(unique=true)
    private String paymentId;
    @Column(unique = true)
    private String orderId;
    @Column
    private PaymentStatus paymentStatus;
    @Column
    private Double amount;


}
