package com.ecommerce.order.RestController;


import com.ecommerce.order.DTO.ProductInfo;
import com.ecommerce.order.DTO.ProductResponse;
import com.ecommerce.order.Enum.DeliveryStatus;
import com.ecommerce.order.FeignClient.ProductAPI;
import com.ecommerce.order.JWT.JwtUtil;
import com.ecommerce.order.Repository.OrderItemRepository;
import com.ecommerce.order.Repository.OrderRepository;
import com.ecommerce.order.entity.Order;
import com.ecommerce.order.entity.OrderItem;
import io.jsonwebtoken.Claims;
import jakarta.transaction.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/order")
public class OrdersRestController {
    private final ProductAPI productAPI;
    private final OrderRepository orderRepository;
    private final JwtUtil jwtUtil;

    public OrdersRestController(ProductAPI productAPI, OrderRepository orderRepository, JwtUtil jwtUtil) {
        this.productAPI = productAPI;
        this.orderRepository = orderRepository;
        this.jwtUtil = jwtUtil;
    }
Logger logger= Logger.getLogger("OrdersRestController.class");

    @PostMapping("/create")
    @Transactional
    public ResponseEntity<String> createOrder(@RequestBody List<ProductInfo> products, @RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401)
                        .body("Missing or invalid Authorization header");
            }

            String jwt = authHeader.substring(7);

            if (!jwtUtil.validateToken(jwt)) {
                return ResponseEntity.status(401)
                        .body("Invalid Token");
            }

            Claims userInfo = jwtUtil.extractAllClaims(jwt);
            Integer userId = userInfo.get("userId", Integer.class);

            if (products == null || products.isEmpty())
                return ResponseEntity.badRequest().body("Product list cannot be empty");

            Order order = new Order();
            order.setUserId(userId);
            order.setAddressId(products.get(0).getAddressId());
            order.setPlacedAt(LocalDateTime.now());

            double totalAmount = 0.0;

            for (ProductInfo product : products) {

                ProductResponse productResponse =
                        productAPI.fetchProductById(product.getProductId(), product.getQuantity());

                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(order);
                orderItem.setProductId(product.getProductId());
                orderItem.setQuantity(product.getQuantity());
                orderItem.setUnitPrice(productResponse.getPrice());

                totalAmount += productResponse.getPrice() * product.getQuantity();

                order.getOrderItem().add(orderItem);
            }

            order.setTotalAmount(totalAmount);
            orderRepository.save(order);
        } catch (Exception e) {
            logger.info("Exception in \"create\" endpoint"+e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Something went wrong");
        }
        return ResponseEntity.status(201)
                .body("Order Placed Successfully");
    }
//GET all orders of logged-in user
    @GetMapping("/myOrders")
    public ResponseEntity<List<Order>> getAllOrderOfUser(@RequestHeader("Authorization")String authHeader){
        List<Order>orders;
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401)
                        .body(null);
            }

            String jwt = authHeader.substring(7);

            if (!jwtUtil.validateToken(jwt)) {
                return ResponseEntity.status(401)
                        .body(null);
            }

            Claims userInfo = jwtUtil.extractAllClaims(jwt);
            Integer userId = userInfo.get("userId", Integer.class);
            orders=orderRepository.findAllByUserId(userId);


        } catch (Exception e) {
            logger.info("Exception in \"myOrders\" endpoint"+e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
        return ResponseEntity.status(200)
                .body(orders);



    }



 //get order details
 @GetMapping("/viewOrder/{orderId}")
    public ResponseEntity<Order> viewOrder(@RequestHeader("Authorization") String authHeader,@PathVariable Integer orderId){
     Order order;
        try {
         if (authHeader == null || !authHeader.startsWith("Bearer ")) {
             return ResponseEntity.status(401)
                     .body(null);
         }

         String jwt = authHeader.substring(7);

         if (!jwtUtil.validateToken(jwt)) {
             return ResponseEntity.status(401)
                     .body(null);
         }

         Claims userInfo = jwtUtil.extractAllClaims(jwt);
         Integer userId = userInfo.get("userId", Integer.class);
         order=orderRepository.findOrderByUserIdAndOrderId(userId,orderId);
            if(order==null)
                return ResponseEntity.status(404).body(null);


     } catch (Exception e) {
         logger.info("Exception in \"viewOrder\" endpoint"+e.getMessage());
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                 .body(null);
     }
     return ResponseEntity.status(200)
             .body(order);
 }


 //Cancel order
@PutMapping("/cancelOrder/{orderId}")
@Transactional
    public ResponseEntity<Order> cancelOrder(@RequestHeader("Authorization") String authHeader,@PathVariable Integer orderId){
    Order order;
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401)
                        .body(null);
            }

            String jwt = authHeader.substring(7);

            if (!jwtUtil.validateToken(jwt)) {
                return ResponseEntity.status(401)
                        .body(null);
            }

            Claims userInfo = jwtUtil.extractAllClaims(jwt);
            Integer userId = userInfo.get("userId", Integer.class);
            order=orderRepository.findOrderByUserIdAndOrderId(userId,orderId);
            if(order==null)
                    return ResponseEntity.status(404).body(null);
            if (order.getDeliveryStatus() == DeliveryStatus.TRANSIT ||
                    order.getDeliveryStatus() == DeliveryStatus.OUTFORDELIVERY ||
                    order.getDeliveryStatus() == DeliveryStatus.DELIVERED) {

                return ResponseEntity.status(400)
                        .body(null);
            }
            order.setDeliveryStatus(DeliveryStatus.CANCELLED);


        } catch (Exception e) {
            logger.info("Exception in \"cancel Order\" endpoint"+e.getMessage());
            return ResponseEntity.status(500)
                    .body(null);
        }
        orderRepository.save(order);
       return ResponseEntity.status(200)
                .body(order);
    }




}