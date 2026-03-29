package com.smartcart.paymentservice.controllers;

import com.smartcart.paymentservice.services.PaymentService;
import com.stripe.exception.StripeException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
public class PaymentController {
    private PaymentService  paymentService;

    public  PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/initiate")
    public String generatePaymentLink(@RequestParam("orderId") Long orderId) throws StripeException {
        return paymentService.generatePaymentLink(orderId);
    }
}
