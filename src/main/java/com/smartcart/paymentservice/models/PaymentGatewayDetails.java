package com.smartcart.paymentservice.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class PaymentGatewayDetails extends BaseModel{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;
    private String paymentLinkId;
    @Enumerated(EnumType.STRING)
    private PaymentGatewayType gateway;
//    @Column(columnDefinition = "TEXT")
//    private String metadata;
}
