package com.ecommerce.order.Repository;

import com.ecommerce.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface OrderRepository extends JpaRepository<Order,Integer> {
    Order findOrderByUserIdAndOrderId(Integer userId,Integer OrderId);
    List<Order> findAllByUserId(Integer userId);
}
