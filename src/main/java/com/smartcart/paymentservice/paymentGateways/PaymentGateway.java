package com.smartcart.paymentservice.paymentGateways;

import com.smartcart.paymentservice.clients.OrderResponseDto;
import com.smartcart.paymentservice.dtos.PaymentGatewayResponse;
import com.smartcart.paymentservice.models.PaymentGatewayType;
import com.stripe.exception.StripeException;

public interface PaymentGateway {
    PaymentGatewayType getGatewayType();
    PaymentGatewayResponse generatePaymentLink(OrderResponseDto order, Long paymentId) throws StripeException;
}
