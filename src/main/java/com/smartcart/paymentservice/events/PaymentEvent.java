package com.smartcart.paymentservice.events;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentEvent {

    private Long orderId;
    private String paymentStatus;

    public PaymentEvent(){}

    public PaymentEvent(Long orderId, String paymentStatus) {
        this.orderId = orderId;
        this.paymentStatus = paymentStatus;
    }
}
