package com.smartcart.paymentservice.models;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Payment extends BaseModel{
    private Long orderId;
    private Long userId;
    private Double amount;
    private PaymentStatus paymentStatus;
    private String transactionId;
}
