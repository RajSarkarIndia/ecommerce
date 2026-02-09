package com.ecommerce.Payment.Controller;

import com.ecommerce.Payment.DTO.PaymentDTO;
import com.ecommerce.Payment.Enum.PaymentStatus;
import com.ecommerce.Payment.entity.Payment;
import com.ecommerce.Payment.repository.PaymentRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
public class PaymentRestController {

    private final PaymentRepository paymentRepository;

    public PaymentRestController(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Value("${stripeKey}")
    private String key;


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
        payment.setStripeSessionId(session.getId());
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setAmount(paymentInfo.getAmount());

        paymentRepository.save(payment);

        return session.getUrl();
    }
}
