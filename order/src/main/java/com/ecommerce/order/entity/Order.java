package com.ecommerce.order.entity;

import com.ecommerce.order.Enum.DeliveryStatus;
import com.ecommerce.order.Enum.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderId;

    @Column(name = "userId", nullable = false)
    private Integer userId;

    @Column(name = "totalAmount", nullable = false)
    private Double totalAmount;
    @Column(name = "addressId", nullable = false)
    private Integer addressId;
    @Column(name = "placedAt", nullable = false)
    private LocalDateTime placedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "paymentStatus", nullable = false)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    @Column(name = "paymentId")
    private Integer paymentId;
    @Column(name="delhivery_Status",nullable=false)
    private DeliveryStatus deliveryStatus=DeliveryStatus.PLACED;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItem=new ArrayList<>(); //One order = multiple products


}
