package com.schoolmanagement.controller;

import com.schoolmanagement.service.MpesaService;
import com.schoolmanagement.service.StripeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments/webhooks")
@RequiredArgsConstructor
@Slf4j
public class PaymentWebhookController {

    private final MpesaService mpesaService;
    private final StripeService stripeService;

    @PostMapping("/mpesa")
    public ResponseEntity<Map<String, String>> mpesaWebhook(@RequestBody Map<String, Object> payload) {
        try {
            log.info("Received M-Pesa webhook: {}", payload);

            // Process M-Pesa callback
            mpesaService.processCallback(payload);

            // Return success response
            return ResponseEntity.ok(Map.of(
                "ResultCode", "0",
                "ResultDesc", "Success"
            ));

        } catch (Exception e) {
            log.error("Error processing M-Pesa webhook: {}", e.getMessage());
            return ResponseEntity.ok(Map.of(
                "ResultCode", "1",
                "ResultDesc", "Error processing webhook"
            ));
        }
    }

    @PostMapping("/stripe")
    public ResponseEntity<String> stripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String signature) {
        try {
            log.info("Received Stripe webhook with signature: {}", signature);

            // Process Stripe webhook
            stripeService.processWebhook(payload, signature);

            return ResponseEntity.ok("Webhook processed successfully");

        } catch (Exception e) {
            log.error("Error processing Stripe webhook: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Webhook processing failed");
        }
    }

    @GetMapping("/mpesa/status/{checkoutRequestId}")
    public ResponseEntity<Map<String, String>> getMpesaStatus(@PathVariable String checkoutRequestId) {
        try {
            log.info("Checking M-Pesa status for: {}", checkoutRequestId);

            String status = mpesaService.querySTKPushStatus(checkoutRequestId);

            return ResponseEntity.ok(Map.of(
                "checkoutRequestId", checkoutRequestId,
                "status", status
            ));

        } catch (Exception e) {
            log.error("Error checking M-Pesa status: {}", e.getMessage());
            return ResponseEntity.ok(Map.of(
                "checkoutRequestId", checkoutRequestId,
                "status", "ERROR"
            ));
        }
    }
}

