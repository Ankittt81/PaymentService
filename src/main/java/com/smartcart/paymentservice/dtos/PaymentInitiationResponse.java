package com.smartcart.paymentservice.dtos;

import com.smartcart.paymentservice.models.PaymentGatewayType;
import com.smartcart.paymentservice.models.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentInitiationResponse {

    private String paymentUrl;

    private String providerReferenceId;

    private PaymentGatewayType gateway;

    private PaymentStatus paymentStatus;
}