package com.smartcart.paymentservice.strategies;

import com.smartcart.paymentservice.models.PaymentGatewayType;
import com.smartcart.paymentservice.paymentGateways.PaymentGateway;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PaymentGatewayFactory {
    private Map<PaymentGatewayType, PaymentGateway> gatewayMap;

    public PaymentGatewayFactory(List<PaymentGateway> gateways) {
        gatewayMap=new HashMap<>();

        for (PaymentGateway g: gateways) {
            gatewayMap.put(g.getGatewayType(),g);
        }
    }

    public PaymentGateway getGateway(PaymentGatewayType type) {
        return gatewayMap.get(type);
    }
}
