package com.smartcart.paymentservice.services;


import com.smartcart.paymentservice.clients.OrderClient;
import com.smartcart.paymentservice.clients.OrderResponseDto;
import com.smartcart.paymentservice.dtos.PaymentGatewayResponse;
import com.smartcart.paymentservice.models.*;
import com.smartcart.paymentservice.paymentGateways.PaymentGateway;
import com.smartcart.paymentservice.repositories.PaymentGatewayDetailsRepository;
import com.smartcart.paymentservice.repositories.PaymentRepository;
import com.smartcart.paymentservice.strategies.GatewaySelector;
import com.smartcart.paymentservice.strategies.PaymentGatewayFactory;
import com.stripe.exception.StripeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PaymentServiceImpl implements PaymentService {

    private PaymentGateway paymentGateway;
    private OrderClient  orderClient;
    private PaymentRepository  paymentRepository;
    private PaymentGatewayDetailsRepository  paymentGatewayDetailsRepository;
    @Autowired
    private GatewaySelector gatewaySelector;
    @Autowired
    private PaymentGatewayFactory  paymentGatewayFactory;


    public PaymentServiceImpl(PaymentGateway paymentGateway, OrderClient orderClient, PaymentRepository paymentRepository, PaymentGatewayDetailsRepository paymentGatewayDetailsRepository) {
        this.paymentGateway = paymentGateway;
        this.orderClient = orderClient;
        this.paymentRepository = paymentRepository;
        this.paymentGatewayDetailsRepository = paymentGatewayDetailsRepository;
    }

    @Override
    public String generatePaymentLink(Long orderId) throws StripeException {
        Optional<Payment> existing=paymentRepository.findByOrderId(orderId);
        if(existing.isPresent()){
            return  existing.get().getPaymentLink();
        }

        OrderResponseDto order = orderClient.getOrderById(orderId);
        if(order == null){
            throw new RuntimeException("Order Not Found");
        }
        if(!order.getPaymentStatus().equals(PaymentStatus.PENDING)){
            throw new RuntimeException("Payment Status Not Pending");
        }
        if(!order.getOrderStatus().equals(OrderStatus.CREATED)){
            throw new RuntimeException("Order Status Not Created");
        }
        Payment payment=new Payment();
        payment.setOrderId(orderId);
        payment.setUserId(order.getUserId());
        payment.setAmount(order.getTotalAmount());
        payment.setPaymentStatus(PaymentStatus.INITIATED);
        payment=paymentRepository.save(payment);

        PaymentGatewayType type= gatewaySelector.choose(order);
        PaymentGateway gateway=paymentGatewayFactory.getGateway(type);

        PaymentGatewayResponse response=gateway.generatePaymentLink(order, payment.getId());

        payment.setPaymentLink(response.getPaymentUrl());
        paymentRepository.save(payment);

        PaymentGatewayDetails details=new PaymentGatewayDetails();
        details.setPayment(payment);
        details.setGateway(type);
        details.setPaymentLinkId(response.getPaymentLinkId());
        paymentGatewayDetailsRepository.save(details);

        return response.getPaymentUrl();
    }
}
