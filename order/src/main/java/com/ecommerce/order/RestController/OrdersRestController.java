package com.ecommerce.order.RestController;


import com.ecommerce.order.DTO.OrderResponse;
import com.ecommerce.order.DTO.PaymentDTO;
import com.ecommerce.order.DTO.ProductInfoForBuying;
import com.ecommerce.order.DTO.ProductResponse;
import com.ecommerce.order.Enum.DeliveryStatus;
import com.ecommerce.order.FeignClient.PaymentAPI;
import com.ecommerce.order.FeignClient.ProductAPI;
import com.ecommerce.order.JWT.JwtUtil;
import com.ecommerce.order.Repository.OrderRepository;
import com.ecommerce.order.entity.Order;
import com.ecommerce.order.entity.OrderItem;
import com.ecommerce.order.mapper.OrderToOrderResponseMapper;
import io.jsonwebtoken.Claims;
import jakarta.transaction.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/order")
public class OrdersRestController {
    private final ProductAPI productAPI;
    private final OrderRepository orderRepository;
    private final JwtUtil jwtUtil;
    private final PaymentAPI paymentAPI;

    public OrdersRestController(ProductAPI productAPI, PaymentAPI paymentAPI, OrderRepository orderRepository, JwtUtil jwtUtil) {
        this.productAPI = productAPI;
        this.orderRepository = orderRepository;
        this.jwtUtil = jwtUtil;
        this.paymentAPI = paymentAPI;
    }

    Logger logger = Logger.getLogger("OrdersRestController.class");

    @PostMapping("/create")
    @Transactional
    public ResponseEntity<String> createOrder(@RequestBody List<ProductInfoForBuying> products, @RequestHeader("Authorization") String authHeader) {
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

            for (ProductInfoForBuying product : products) {

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
            //return the URL and let the user do payment. Place the order first, for convinience.

            String paymentUrl = paymentAPI.createSessionUrl(new PaymentDTO(null, order.getOrderId(), null, order.getTotalAmount()));
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(paymentUrl);


        } catch (Exception e) {
            logger.info("Exception in \"create\" endpoint" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Something went wrong");
        }

    }

    //GET all orders of logged-in user
    @GetMapping("/myOrders")
    public ResponseEntity<List<OrderResponse>> getAllOrderOfUser(@RequestHeader("Authorization") String authHeader) {
        List<Order> orders;
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
            orders = orderRepository.findAllByUserId(userId);
            List<OrderResponse>allProduct= OrderToOrderResponseMapper.mapOrders(orders);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(allProduct);

        } catch (Exception e) {
            logger.info("Exception in \"myOrders\" endpoint" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }



    }


    //get order details
    @GetMapping("/viewOrder/{orderId}")
    public ResponseEntity<Order> viewOrder(@RequestHeader("Authorization") String authHeader, @PathVariable Integer orderId) {
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
            order = orderRepository.findOrderByUserIdAndOrderId(userId, orderId);
            if (order == null)
                return ResponseEntity.status(404).body(null);


        } catch (Exception e) {
            logger.info("Exception in \"viewOrder\" endpoint" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
        return ResponseEntity.status(200)
                .body(order);
    }


    //Cancel order
    @PutMapping("/cancelOrder/{orderId}")
    @Transactional
    public ResponseEntity<Order> cancelOrder(@RequestHeader("Authorization") String authHeader, @PathVariable Integer orderId) {
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
            order = orderRepository.findOrderByUserIdAndOrderId(userId, orderId);
            if (order == null)
                return ResponseEntity.status(404).body(null);
            if (order.getDeliveryStatus() == DeliveryStatus.TRANSIT ||
                    order.getDeliveryStatus() == DeliveryStatus.OUTFORDELIVERY ||
                    order.getDeliveryStatus() == DeliveryStatus.DELIVERED) {

                return ResponseEntity.status(400)
                        .body(null);
            }
            order.setDeliveryStatus(DeliveryStatus.CANCELLED);


        } catch (Exception e) {
            logger.info("Exception in \"cancel Order\" endpoint" + e.getMessage());
            return ResponseEntity.status(500)
                    .body(null);
        }
        orderRepository.save(order);
        return ResponseEntity.status(200)
                .body(order);
    }


    //Payment received

    @PutMapping("/paymentReceived")
    public ResponseEntity<Void> paymentStatus(@RequestHeader("PaymentKey") String paymentKey, @RequestBody PaymentDTO paymentInfo) {
        if (!paymentKey.equals("paymentKey")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(null);
        }
        Order order = orderRepository.findById(paymentInfo.getOrderId()).orElse(null);
        if (order != null) {
            order.setPaymentId(paymentInfo.getPaymentId());
            order.setPaymentStatus(paymentInfo.getPaymentStatus());
            orderRepository.save(order);
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(null);
    }


}