package com.smartcart.paymentservice.clients;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderItemResponseDto {
    private Long  orderId;
    private Long orderItemId;
    private Long productId;
    private Long variantId;
    private String productTitle;
    private String productImageUrl;
    private String variantAttributes;
    private Integer quantity;
    private BigDecimal priceAtPurchase;
    private BigDecimal totalPrice;
}
