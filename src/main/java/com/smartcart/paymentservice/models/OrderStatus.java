package com.smartcart.paymentservice.models;

public enum OrderStatus {
    CREATED,
    PAYMENT_PENDING,
    CONFIRMED,
    SHIPPED,
    DELIVERED,
    CANCELED
}
