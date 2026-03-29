package com.smartcart.paymentservice.paymentGateways;

import com.smartcart.paymentservice.clients.OrderItemResponseDto;
import com.smartcart.paymentservice.clients.OrderResponseDto;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentLink;
import com.stripe.model.Price;
import com.stripe.model.Product;
import com.stripe.param.PaymentLinkCreateParams;
import com.stripe.param.PriceCreateParams;
import com.stripe.param.ProductCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class StripePaymentGateway implements PaymentGateway {
    @Value("${stripe.key}")
    private String stripeKey;
    public String generatePaymentLink(OrderResponseDto order) throws StripeException {


        Stripe.apiKey =stripeKey;
        // Builder for payment link
        PaymentLinkCreateParams.Builder paramsBuilder =
                PaymentLinkCreateParams.builder();

        for(OrderItemResponseDto item : order.getItems()){
            // 1. Create Product (dynamic)
            ProductCreateParams productParams =
                    ProductCreateParams.builder()
                            .setName(item.getProductName())
                            .build();

            Product product = Product.create(productParams);

            // 2. Create Price (per item)
            PriceCreateParams priceParams =
                    PriceCreateParams.builder()
                            .setCurrency("inr")
                            .setUnitAmount(
                                    item.getPriceAtPurchase()
                                            .multiply(BigDecimal.valueOf(100)) // convert to paise
                                            .longValue()
                            )
                            .setProduct(product.getId())
                            .build();

            Price price = Price.create(priceParams);

            // 3. Add line item
            paramsBuilder.addLineItem(
                    PaymentLinkCreateParams.LineItem.builder()
                            .setPrice(price.getId())
                            .setQuantity(item.getQuantity().longValue())
                            .build()
            );
        }

        // 4. Create Payment Link
        PaymentLink paymentLink = PaymentLink.create(paramsBuilder.build());

        return paymentLink.toString();
    }
}
