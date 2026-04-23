package com.smartcart.paymentservice.strategies;

import com.smartcart.paymentservice.clients.OrderResponseDto;
import com.smartcart.paymentservice.models.PaymentGatewayType;
import org.springframework.stereotype.Component;

@Component
public class GatewaySelector {
    public PaymentGatewayType choose(OrderResponseDto order){
        return PaymentGatewayType.STRIPE;
    }
}
