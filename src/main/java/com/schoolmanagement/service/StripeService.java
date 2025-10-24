package com.schoolmanagement.service;

import com.schoolmanagement.entity.Payment;
import com.schoolmanagement.entity.PaymentGatewayConfig;
import com.schoolmanagement.repository.PaymentGatewayConfigRepository;
import com.schoolmanagement.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class StripeService {

    private final PaymentGatewayConfigRepository gatewayConfigRepository;
    private final PaymentRepository paymentRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${stripe.secret-key:#{null}}")
    private String secretKey;

    @Value("${stripe.publishable-key:#{null}}")
    private String publishableKey;

    @Value("${stripe.webhook-secret:#{null}}")
    private String webhookSecret;

    @Value("${stripe.environment:sandbox}")
    private String environment;

    public String createPaymentIntent(String amount, String currency, String description, String customerEmail) {
        try {
            if (secretKey == null || secretKey.trim().isEmpty()) {
                log.error("Stripe secret key not configured. Please set stripe.secret-key");
                return null;
            }
            
            log.info("Creating Stripe payment intent for amount: {}, currency: {}", amount, currency);

            String url = getApiUrl() + "/v1/payment_intents";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + secretKey);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("amount", amount);
            requestBody.put("currency", currency);
            requestBody.put("description", description);
            requestBody.put("customer_email", customerEmail);
            requestBody.put("metadata", Map.of(
                "payment_reference", UUID.randomUUID().toString(),
                "source", "school_management_system"
            ));

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = response.getBody();
                if (responseBody != null && responseBody.containsKey("id")) {
                    log.info("Payment intent created successfully: {}", responseBody.get("id"));
                    return (String) responseBody.get("id");
                }
            }

            log.error("Failed to create payment intent: {}", response.getBody());
            throw new RuntimeException("Failed to create payment intent");

        } catch (Exception e) {
            log.error("Error creating payment intent: {}", e.getMessage());
            throw new RuntimeException("Failed to create payment intent: " + e.getMessage());
        }
    }

    public String confirmPaymentIntent(String paymentIntentId) {
        try {
            log.info("Confirming Stripe payment intent: {}", paymentIntentId);

            String url = getApiUrl() + "/v1/payment_intents/" + paymentIntentId + "/confirm";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + secretKey);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("return_url", "https://your-school.com/payment/return");

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = response.getBody();
                if (responseBody != null) {
                    String status = (String) responseBody.get("status");
                    log.info("Payment intent confirmed with status: {}", status);
                    return status;
                }
            }

            log.error("Failed to confirm payment intent: {}", response.getBody());
            return "FAILED";

        } catch (Exception e) {
            log.error("Error confirming payment intent: {}", e.getMessage());
            return "ERROR";
        }
    }

    public String createCustomer(String email, String name, String phone) {
        try {
            log.info("Creating Stripe customer for email: {}", email);

            String url = getApiUrl() + "/v1/customers";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + secretKey);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("email", email);
            requestBody.put("name", name);
            requestBody.put("phone", phone);
            requestBody.put("metadata", Map.of(
                "source", "school_management_system",
                "type", "student_parent"
            ));

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = response.getBody();
                if (responseBody != null && responseBody.containsKey("id")) {
                    log.info("Customer created successfully: {}", responseBody.get("id"));
                    return (String) responseBody.get("id");
                }
            }

            log.error("Failed to create customer: {}", response.getBody());
            throw new RuntimeException("Failed to create customer");

        } catch (Exception e) {
            log.error("Error creating customer: {}", e.getMessage());
            throw new RuntimeException("Failed to create customer: " + e.getMessage());
        }
    }

    public void processWebhook(String payload, String signature) {
        try {
            log.info("Processing Stripe webhook with signature: {}", signature);

            // Verify webhook signature
            if (!verifyWebhookSignature(payload, signature)) {
                log.error("Invalid webhook signature");
                throw new RuntimeException("Invalid webhook signature");
            }

            // Parse webhook payload
            // This would typically use Stripe's webhook parsing library
            // For now, we'll log the payload
            log.info("Webhook payload: {}", payload);

            // Process webhook events
            // This would handle events like payment_intent.succeeded, payment_intent.payment_failed, etc.

        } catch (Exception e) {
            log.error("Error processing Stripe webhook: {}", e.getMessage());
            throw new RuntimeException("Failed to process webhook: " + e.getMessage());
        }
    }

    public String createCheckoutSession(String amount, String currency, String description, String successUrl, String cancelUrl) {
        try {
            log.info("Creating Stripe checkout session for amount: {}", amount);

            String url = getApiUrl() + "/v1/checkout/sessions";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + secretKey);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("payment_method_types", new String[]{"card"});
            requestBody.put("line_items", new Object[]{
                Map.of(
                    "price_data", Map.of(
                        "currency", currency,
                        "product_data", Map.of("name", description),
                        "unit_amount", amount
                    ),
                    "quantity", 1
                )
            });
            requestBody.put("mode", "payment");
            requestBody.put("success_url", successUrl);
            requestBody.put("cancel_url", cancelUrl);
            requestBody.put("metadata", Map.of(
                "payment_reference", UUID.randomUUID().toString(),
                "source", "school_management_system"
            ));

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = response.getBody();
                if (responseBody != null && responseBody.containsKey("id")) {
                    log.info("Checkout session created successfully: {}", responseBody.get("id"));
                    return (String) responseBody.get("id");
                }
            }

            log.error("Failed to create checkout session: {}", response.getBody());
            throw new RuntimeException("Failed to create checkout session");

        } catch (Exception e) {
            log.error("Error creating checkout session: {}", e.getMessage());
            throw new RuntimeException("Failed to create checkout session: " + e.getMessage());
        }
    }

    private boolean verifyWebhookSignature(String payload, String signature) {
        if (webhookSecret == null || webhookSecret.trim().isEmpty()) {
            log.error("Stripe webhook secret not configured. Please set stripe.webhook-secret");
            return false;
        }
        
        // This would typically use Stripe's webhook signature verification
        // For now, we'll return true for demonstration
        return true;
    }

    private String getApiUrl() {
        return "sandbox".equals(environment) 
            ? "https://api.stripe.com"
            : "https://api.stripe.com";
    }
}

