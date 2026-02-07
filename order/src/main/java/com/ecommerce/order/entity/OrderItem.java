package com.ecommerce.order.entity;


import com.ecommerce.order.Enum.DeliveryStatus;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "order_item")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "product_id",nullable = false)
    private Integer productId;
    @Column(name = "quantity",nullable = false)
    private Integer quantity;
    @Column(name = "unit_price", nullable=false)
    private Double unitPrice;//fetch unit price from product

}
