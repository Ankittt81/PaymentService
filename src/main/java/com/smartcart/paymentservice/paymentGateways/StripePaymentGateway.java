package com.smartcart.paymentservice.paymentGateways;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartcart.paymentservice.clients.OrderItemResponseDto;
import com.smartcart.paymentservice.clients.OrderResponseDto;
import com.smartcart.paymentservice.dtos.PaymentGatewayResponse;
import com.smartcart.paymentservice.models.PaymentGatewayType;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentLink;
import com.stripe.model.Price;
import com.stripe.model.Product;
import com.stripe.param.PaymentLinkCreateParams;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import java.math.BigDecimal;
import java.util.Map;

@Component
public class StripePaymentGateway implements PaymentGateway {
    @Value("${stripe.key}")
    private String stripeKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeKey;
    }

    @Override
    public PaymentGatewayType getGatewayType() {
        return PaymentGatewayType.STRIPE;
    }

    public PaymentGatewayResponse generatePaymentLink(OrderResponseDto order, Long paymentId) throws StripeException {
        try{
            PaymentLinkCreateParams.Builder builder =
                    PaymentLinkCreateParams.builder();
            for (OrderItemResponseDto item : order.getItems()) {
                Product product = Product.create(
                        Map.of("name", item.getProductName())
                );
                Price price = Price.create(
                        Map.of(
                                "unit_amount", (item.getPriceAtPurchase()
                                        .multiply(BigDecimal.valueOf(100))
                                        .longValue()),
                                "currency", "inr",
                                "product", product.getId()
                        )
                );
                builder.addLineItem(
                        PaymentLinkCreateParams.LineItem.builder()
                                .setPrice(price.getId())
                                .setQuantity((long) item.getQuantity())
                                .build()
                );
            }
//            builder.setClientReferenceId(paymentId.toString());
//            builder.setMetadata(Map.of("paymentId", paymentId.toString()));
            PaymentLink paymentLink = PaymentLink.create(builder.build());

            PaymentGatewayResponse response = new PaymentGatewayResponse();
            response.setPaymentUrl(paymentLink.getUrl());
            response.setPaymentLinkId(paymentLink.getId());

            return response;

        }catch (Exception e){
            throw new RuntimeException("Stripe error: " + e.getMessage());
        }

    }
}
