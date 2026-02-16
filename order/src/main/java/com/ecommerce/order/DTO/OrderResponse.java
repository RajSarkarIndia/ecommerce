package com.ecommerce.order.DTO;

import com.ecommerce.order.Enum.DeliveryStatus;
import com.ecommerce.order.Enum.PaymentStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {

    private Integer orderId;
    private Double totalAmount;
    private Integer addressId;
    private LocalDateTime placedAt;

    private PaymentStatus paymentStatus;
    private DeliveryStatus deliveryStatus;

    private List<OrderItemResponse> items;
}

