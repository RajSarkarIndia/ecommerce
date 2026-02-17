package com.ecommerce.order.mapper;

import com.ecommerce.order.DTO.OrderItemResponse;
import com.ecommerce.order.DTO.OrderResponse;
import com.ecommerce.order.entity.Order;
import com.ecommerce.order.entity.OrderItem;

import java.util.ArrayList;
import java.util.List;

public class OrderToOrderResponse {

    public List<OrderItemResponse> OrderItemToOrderItemResponse(List<OrderItem>orderItem){
        List<OrderItemResponse> orderItemResponses=new ArrayList<>();
        for(OrderItem order:orderItem){
            OrderItemResponse orderItemResponseMapper=new OrderItemResponse();
            orderItemResponseMapper.setProductId(order.getProductId());
            orderItemResponseMapper.setUnitPrice(order.getUnitPrice());
            orderItemResponseMapper.setQuantity(order.getQuantity());
            orderItemResponses.add(orderItemResponseMapper);
}

return orderItemResponses;
    }



    public OrderResponse mapIt(Order order){
        OrderResponse orderResponse=new OrderResponse();
        orderResponse.setOrderId(order.getOrderId());
        orderResponse.setTotalAmount(order.getTotalAmount());
        orderResponse.setAddressId(order.getAddressId());
        orderResponse.setPlacedAt(order.getPlacedAt());
        orderResponse.setPaymentStatus(order.getPaymentStatus());
        orderResponse.setDeliveryStatus(order.getDeliveryStatus());
        orderResponse.setItems(OrderItemToOrderItemResponse(order.getOrderItem()));
        return orderResponse;

    }

}
