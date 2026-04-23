package com.smartcart.paymentservice.services;

public interface WebhookService {
   String handleStripeWebhook(String payload,String sigHeader);
}
