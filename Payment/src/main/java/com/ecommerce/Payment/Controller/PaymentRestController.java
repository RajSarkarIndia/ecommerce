package com.ecommerce.Payment.Controller;

import com.ecommerce.Payment.API.OrderApi;
import com.ecommerce.Payment.DTO.PaymentDTO;
import com.ecommerce.Payment.Enum.PaymentStatus;
import com.ecommerce.Payment.entity.Payment;
import com.ecommerce.Payment.repository.PaymentRepository;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequestMapping("/payment")
public class PaymentRestController {

    Logger logger = Logger.getLogger("Payment Controller");

    private final PaymentRepository paymentRepository;
    private final OrderApi orderApi;

    public PaymentRestController(PaymentRepository paymentRepository, OrderApi orderApi) {
        this.paymentRepository = paymentRepository;
        this.orderApi = orderApi;
    }

    @Value("${stripeKey}")
    private String key;

    @Value("${webhookSecret}")
    private String webhookSecret;

    @PostConstruct
    public void init() {
        Stripe.apiKey = key;
    }

    @PostMapping("/create")
    public String createSessionUrl(@RequestBody PaymentDTO paymentInfo) throws StripeException {

        long amountInCents = Math.round(paymentInfo.getAmount() * 100);

        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl("http://localhost:8080/success")
                        .setCancelUrl("http://localhost:8080/cancel")
                        .putMetadata("orderId", paymentInfo.getOrderId())
                        .addLineItem(
                                SessionCreateParams.LineItem.builder()
                                        .setQuantity(1L)
                                        .setPriceData(
                                                SessionCreateParams.LineItem.PriceData.builder()
                                                        .setCurrency("usd")
                                                        .setUnitAmount(amountInCents)
                                                        .setProductData(
                                                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                        .setName("Cart Purchase")
                                                                        .build()
                                                        )
                                                        .build()
                                        )
                                        .build()
                        )
                        .build();

        Session session = Session.create(params);

        Payment payment = new Payment();
        payment.setOrderId(paymentInfo.getOrderId());
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setAmount(paymentInfo.getAmount());

        paymentRepository.save(payment);

        return session.getUrl();
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(
            HttpServletRequest request,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        String payload;
        Event event;

        try {
            payload = new String(request.getInputStream().readAllBytes());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Failed to read payload");
        }

        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Webhook error");
        }

        EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();

        if (!deserializer.getObject().isPresent()) {
            return ResponseEntity.badRequest().body("Invalid event data");
        }

        logger.warning(event.getType());

        switch (event.getType()) {

            case "checkout.session.completed": {

                Session session = (Session) deserializer.getObject().get();

                if (!"paid".equals(session.getPaymentStatus())) {
                    logger.warning("Session completed but payment not marked as paid");
                    break;
                }

                Map<String, String> metadata = session.getMetadata();
                String orderId = metadata.get("orderId");

                if (orderId == null) {
                    logger.warning("No orderId found in metadata");
                    break;
                }

                String paymentId = session.getPaymentIntent();

                Payment paymentInfo = paymentRepository.findByOrderId(orderId);

                if (paymentInfo != null) {

                    if (paymentInfo.getPaymentStatus() == PaymentStatus.SUCCESS) {
                        logger.info("Payment already processed for order " + orderId);
                        break;
                    }

                    paymentInfo.setPaymentId(paymentId);
                    paymentInfo.setPaymentStatus(PaymentStatus.SUCCESS);
                    paymentRepository.save(paymentInfo);

                    PaymentDTO dto = new PaymentDTO();
                    dto.setOrderId(orderId);
                    dto.setPaymentId(paymentId);
                    dto.setPaymentStatus(PaymentStatus.SUCCESS);

                    orderApi.paymentStatus("paymentKey",dto);
                } else {
                    logger.warning("Payment record not found for orderId " + orderId);
                }

                break;
            }

            default:
                logger.info("Unhandled event type: " + event.getType());
        }

        return ResponseEntity.ok("Webhook received");
    }
}
