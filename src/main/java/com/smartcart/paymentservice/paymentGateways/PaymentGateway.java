package com.smartcart.paymentservice.paymentGateways;

import com.smartcart.paymentservice.clients.OrderResponseDto;
import com.stripe.exception.StripeException;

public interface PaymentGateway {
    String generatePaymentLink(OrderResponseDto order) throws StripeException;
}
