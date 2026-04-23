package com.smartcart.paymentservice.repositories;

import com.smartcart.paymentservice.models.PaymentGatewayDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentGatewayDetailsRepository extends JpaRepository<PaymentGatewayDetails, Long> {
    Optional<PaymentGatewayDetails> findByPaymentLinkId(String paymentLinkId);
}
