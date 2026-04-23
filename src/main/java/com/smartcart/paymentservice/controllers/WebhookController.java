package com.smartcart.paymentservice.controllers;

import com.smartcart.paymentservice.services.WebhookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhooks")
public class WebhookController {
    private WebhookService webhookService;

    public WebhookController(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @PostMapping()
    public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload,@RequestHeader("Stripe-Signature") String sigHeader) {
      //  System.out.println("RAW PAYLOAD:\n" + payload);
        return new ResponseEntity<>(webhookService.handleStripeWebhook(payload,sigHeader), HttpStatus.OK);
    }
}
