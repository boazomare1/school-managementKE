package com.schoolmanagement.controller;

import com.schoolmanagement.service.MpesaService;
import com.schoolmanagement.service.StripeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments/test")
@RequiredArgsConstructor
@Slf4j
public class PaymentTestController {

    private final MpesaService mpesaService;
    private final StripeService stripeService;

    @PostMapping("/mpesa/stk-push")
    public ResponseEntity<Map<String, String>> initiateMpesaSTKPush(
            @RequestParam String phoneNumber,
            @RequestParam String amount,
            @RequestParam String accountReference,
            @RequestParam String transactionDescription) {
        try {
            log.info("Initiating M-Pesa STK Push for phone: {}, amount: {}", phoneNumber, amount);
            
            String checkoutRequestId = mpesaService.initiateSTKPush(phoneNumber, amount, accountReference, transactionDescription);
            
            return ResponseEntity.ok(Map.of(
                "success", "true",
                "message", "STK Push initiated successfully",
                "checkoutRequestId", checkoutRequestId
            ));
            
        } catch (Exception e) {
            log.error("Error initiating STK Push: {}", e.getMessage());
            return ResponseEntity.ok(Map.of(
                "success", "false",
                "message", "STK Push failed: " + e.getMessage(),
                "checkoutRequestId", "null"
            ));
        }
    }

    @PostMapping("/stripe/payment-intent")
    public ResponseEntity<Map<String, String>> createStripePaymentIntent(
            @RequestParam String amount,
            @RequestParam String currency,
            @RequestParam String description) {
        try {
            log.info("Creating Stripe Payment Intent for amount: {}, currency: {}", amount, currency);
            
            String clientSecret = stripeService.createPaymentIntent(amount, currency, description, "test@example.com");
            
            return ResponseEntity.ok(Map.of(
                "success", "true",
                "message", "Payment Intent created successfully",
                "clientSecret", clientSecret
            ));
            
        } catch (Exception e) {
            log.error("Error creating Payment Intent: {}", e.getMessage());
            return ResponseEntity.ok(Map.of(
                "success", "false",
                "message", "Payment Intent creation failed: " + e.getMessage(),
                "clientSecret", "null"
            ));
        }
    }

    @GetMapping("/mpesa/status/{checkoutRequestId}")
    public ResponseEntity<Map<String, String>> getMpesaStatus(@PathVariable String checkoutRequestId) {
        try {
            log.info("Checking M-Pesa status for: {}", checkoutRequestId);
            
            String status = mpesaService.querySTKPushStatus(checkoutRequestId);
            
            return ResponseEntity.ok(Map.of(
                "success", "true",
                "checkoutRequestId", checkoutRequestId,
                "status", status
            ));
            
        } catch (Exception e) {
            log.error("Error checking M-Pesa status: {}", e.getMessage());
            return ResponseEntity.ok(Map.of(
                "success", "false",
                "checkoutRequestId", checkoutRequestId,
                "status", "ERROR: " + e.getMessage()
            ));
        }
    }
}
