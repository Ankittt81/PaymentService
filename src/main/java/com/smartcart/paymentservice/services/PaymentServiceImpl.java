package com.smartcart.paymentservice.services;

import com.smartcart.paymentservice.clients.OrderClient;
import com.smartcart.paymentservice.clients.OrderResponseDto;
import com.smartcart.paymentservice.models.OrderStatus;
import com.smartcart.paymentservice.models.PaymentStatus;
import com.smartcart.paymentservice.paymentGateways.PaymentGateway;
import com.stripe.exception.StripeException;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {

    private PaymentGateway paymentGateway;
    private OrderClient  orderClient;

    public PaymentServiceImpl(PaymentGateway paymentGateway, OrderClient orderClient) {
        this.paymentGateway = paymentGateway;
        this.orderClient = orderClient;
    }

    @Override
    public String generatePaymentLink(Long orderId) throws StripeException {
        OrderResponseDto orderResponseDto = orderClient.getOrderById(orderId);
        if(orderResponseDto == null){
            throw new RuntimeException("Order Not Found");
        }
        if(!orderResponseDto.getPaymentStatus().equals(PaymentStatus.PENDING)){
            throw new RuntimeException("Payment Status Not Pending");
        }
        if(!orderResponseDto.getOrderStatus().equals(OrderStatus.CREATED)){
            throw new RuntimeException("Order Status Not Created");
        }
        return paymentGateway.generatePaymentLink(orderResponseDto);
    }
}
