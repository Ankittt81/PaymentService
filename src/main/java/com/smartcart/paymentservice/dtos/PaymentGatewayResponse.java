package com.smartcart.paymentservice.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentGatewayResponse {
    private String paymentUrl;
    private String paymentLinkId;
}
