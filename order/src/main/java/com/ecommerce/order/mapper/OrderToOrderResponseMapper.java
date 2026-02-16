package com.ecommerce.order.mapper;

import com.ecommerce.order.DTO.OrderItemResponse;
import com.ecommerce.order.DTO.OrderResponse;
import com.ecommerce.order.entity.Order;
import com.ecommerce.order.entity.OrderItem;

import java.util.ArrayList;
import java.util.List;

public class OrderToOrderResponseMapper {

    public static List<OrderResponse> mapOrders(List<Order> orders) {

        List<OrderResponse> orderResponses = new ArrayList<>();

        if (orders == null || orders.isEmpty()) {
            return orderResponses;
        }

        for (Order order : orders) {

            List<OrderItemResponse> items = new ArrayList<>();

            for (OrderItem item : order.getOrderItem()) {
                OrderItemResponse itemResponse = new OrderItemResponse(
                        item.getProductId(),
                        item.getUnitPrice(),
                        item.getQuantity()
                );
                items.add(itemResponse);
            }

            OrderResponse response = new OrderResponse(
                    order.getOrderId(),
                    order.getTotalAmount(),
                    order.getAddressId(),
                    order.getPlacedAt(),
                    order.getPaymentStatus(),
                    order.getDeliveryStatus(),
                    items
            );

            orderResponses.add(response);
        }

        return orderResponses;
    }
}
