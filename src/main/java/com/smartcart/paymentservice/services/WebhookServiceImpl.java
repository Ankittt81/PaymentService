package com.smartcart.paymentservice.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.smartcart.paymentservice.events.PaymentEvent;
import com.smartcart.paymentservice.models.Payment;
import com.smartcart.paymentservice.models.PaymentGatewayDetails;
import com.smartcart.paymentservice.models.PaymentStatus;
import com.smartcart.paymentservice.repositories.PaymentGatewayDetailsRepository;
import com.smartcart.paymentservice.repositories.PaymentRepository;
import com.stripe.model.Event;

import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;

@Service
public class WebhookServiceImpl implements WebhookService {
    @Value("${stripe.webhook.secret}")
    private String endpointSecret;
    private PaymentGatewayDetailsRepository detailsRepository;
    private KafkaTemplate<String,String> kafkaTemplate;
    private PaymentRepository paymentRepository;

    public WebhookServiceImpl(PaymentGatewayDetailsRepository detailsRepository, PaymentRepository paymentRepository, KafkaTemplate<String,String> kafkaTemplate) {
        this.detailsRepository = detailsRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.paymentRepository = paymentRepository;
    }

    @Override
    public String handleStripeWebhook(String payload, String sigHeader) {

        Event event;

        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (Exception e) {
            throw new RuntimeException("Invalid Webhook Signature", e);
        }

        System.out.println("EVENT TYPE: " + event.getType());

        // ✅ Step 1: filter only required events
        if (!"checkout.session.completed".equals(event.getType()) &&
                !"checkout.session.expired".equals(event.getType())) {
            return "ignored";
        }

        ObjectMapper mapper = new ObjectMapper();

        try {
            // ✅ Step 2: parse raw JSON safely
            String rawJson = event.getDataObjectDeserializer().getRawJson();
            System.out.println("RAW JSON: " + rawJson);

            JsonNode node = mapper.readTree(rawJson);

            // ✅ Step 3: extract required fields
            String paymentLinkId = node.get("payment_link").asText();
            String paymentIntent = node.get("payment_intent").asText();
            long amountTotal = node.get("amount_total").asLong();

            System.out.println("WEBHOOK paymentLinkId: " + paymentLinkId);

            // ✅ Step 4: fetch payment
            PaymentGatewayDetails details = detailsRepository
                    .findByPaymentLinkId(paymentLinkId)
                    .orElseThrow(() -> new RuntimeException("Payment Link Not Found"));

            Payment payment = details.getPayment();

            // ================= SUCCESS =================
            if ("checkout.session.completed".equals(event.getType())) {

                if (payment.getPaymentStatus() == PaymentStatus.SUCCESS) {
                    System.out.println("Duplicate webhook ignored for paymentLinkId: " + paymentLinkId);
                    return "Already processed";
                }

                payment.setPaymentStatus(PaymentStatus.SUCCESS);
                payment.setTransactionId(paymentIntent);

                BigDecimal stripeAmount = BigDecimal.valueOf(amountTotal)
                        .divide(BigDecimal.valueOf(100));

                if (payment.getAmount().compareTo(stripeAmount) != 0) {
                    throw new RuntimeException("Amount mismatch detected!");
                }

                paymentRepository.save(payment);

                try {
                    String message=mapper.writeValueAsString(new PaymentEvent(payment.getOrderId(), "SUCCESS"));
                    kafkaTemplate.send("payment-topic",message);

                    System.out.println("Payment SUCCESS processed for orderId: " + payment.getOrderId());
                } catch (Exception e) {
                    System.out.println("Kafka failed: " + e.getMessage());
                }
            }

            // ================= FAILURE =================
            if ("checkout.session.expired".equals(event.getType())) {

                if (payment.getPaymentStatus() == PaymentStatus.FAILED) {
                    return "Already processed";
                }

                payment.setPaymentStatus(PaymentStatus.FAILED);
                paymentRepository.save(payment);

                try {
                    String message=mapper.writeValueAsString(new PaymentEvent(payment.getOrderId(), "FAILED"));
                    kafkaTemplate.send("payment-topic",message);

                    System.out.println("Payment FAILED for orderId: " + payment.getOrderId());
                } catch (Exception e) {
                    System.out.println("Kafka failed: " + e.getMessage());
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Webhook processing failed", e);
        }

        return "processed";
    }
}