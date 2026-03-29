package com.smartcart.paymentservice.clients;


import com.smartcart.paymentservice.models.OrderStatus;
import com.smartcart.paymentservice.models.PaymentStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class OrderResponseDto {
    private Long orderId;
    private Long userId;
    private BigDecimal totalAmount;

    private OrderStatus orderStatus;
    private PaymentStatus paymentStatus;

    private String shippingAddress;

    private List<OrderItemResponseDto> items;
}
