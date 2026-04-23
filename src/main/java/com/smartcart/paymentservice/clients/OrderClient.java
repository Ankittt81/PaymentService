package com.smartcart.paymentservice.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "order-service",url = "${order.service.url}")
public interface OrderClient {

    @GetMapping("/orders")
    OrderResponseDto getOrderById(@RequestParam("orderId") Long orderId);
}
