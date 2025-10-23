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

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MpesaService {

    private final PaymentGatewayConfigRepository gatewayConfigRepository;
    private final PaymentRepository paymentRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${mpesa.consumer-key:}")
    private String consumerKey;

    @Value("${mpesa.consumer-secret:}")
    private String consumerSecret;

    @Value("${mpesa.short-code:}")
    private String shortCode;

    @Value("${mpesa.pass-key:}")
    private String passKey;

    @Value("${mpesa.callback-url:}")
    private String callbackUrl;

    @Value("${mpesa.environment:sandbox}")
    private String environment;

    public String initiateSTKPush(String phoneNumber, String amount, String accountReference, String transactionDescription) {
        try {
            log.info("Initiating M-Pesa STK Push for phone: {}, amount: {}", phoneNumber, amount);

            // Get access token
            String accessToken = getAccessToken();
            if (accessToken == null) {
                throw new RuntimeException("Failed to get access token");
            }

            // Prepare STK Push request
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String password = generatePassword(timestamp);
            String requestId = UUID.randomUUID().toString();

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("BusinessShortCode", shortCode);
            requestBody.put("Password", password);
            requestBody.put("Timestamp", timestamp);
            requestBody.put("TransactionType", "CustomerPayBillOnline");
            requestBody.put("Amount", amount);
            requestBody.put("PartyA", phoneNumber);
            requestBody.put("PartyB", shortCode);
            requestBody.put("PhoneNumber", phoneNumber);
            requestBody.put("CallBackURL", callbackUrl);
            requestBody.put("AccountReference", accountReference);
            requestBody.put("TransactionDesc", transactionDescription);

            // Make API call
            String url = getApiUrl() + "/mpesa/stkpush/v1/processrequest";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = response.getBody();
                if (responseBody != null && "0".equals(responseBody.get("ResponseCode"))) {
                    log.info("STK Push initiated successfully: {}", responseBody.get("CheckoutRequestID"));
                    return (String) responseBody.get("CheckoutRequestID");
                } else {
                    log.error("STK Push failed: {}", responseBody);
                    throw new RuntimeException("STK Push failed: " + responseBody.get("ResponseDescription"));
                }
            } else {
                throw new RuntimeException("HTTP error: " + response.getStatusCode());
            }

        } catch (Exception e) {
            log.error("Error initiating STK Push: {}", e.getMessage());
            throw new RuntimeException("Failed to initiate STK Push: " + e.getMessage());
        }
    }

    public String querySTKPushStatus(String checkoutRequestId) {
        try {
            log.info("Querying STK Push status for: {}", checkoutRequestId);

            String accessToken = getAccessToken();
            if (accessToken == null) {
                throw new RuntimeException("Failed to get access token");
            }

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String password = generatePassword(timestamp);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("BusinessShortCode", shortCode);
            requestBody.put("Password", password);
            requestBody.put("Timestamp", timestamp);
            requestBody.put("CheckoutRequestID", checkoutRequestId);

            String url = getApiUrl() + "/mpesa/stkpushquery/v1/query";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = response.getBody();
                if (responseBody != null && "0".equals(responseBody.get("ResponseCode"))) {
                    log.info("STK Push query successful: {}", responseBody);
                    return (String) responseBody.get("ResultDesc");
                } else {
                    log.error("STK Push query failed: {}", responseBody);
                    return "FAILED";
                }
            } else {
                throw new RuntimeException("HTTP error: " + response.getStatusCode());
            }

        } catch (Exception e) {
            log.error("Error querying STK Push status: {}", e.getMessage());
            return "ERROR";
        }
    }

    public void processCallback(Map<String, Object> callbackData) {
        try {
            log.info("Processing M-Pesa callback: {}", callbackData);

            String resultCode = (String) callbackData.get("ResultCode");
            String checkoutRequestId = (String) callbackData.get("CheckoutRequestID");
            String merchantRequestId = (String) callbackData.get("MerchantRequestID");

            // Find payment by checkout request ID
            // This would typically be stored when initiating the STK Push
            // For now, we'll log the callback data
            log.info("Callback processed - ResultCode: {}, CheckoutRequestID: {}", resultCode, checkoutRequestId);

            if ("0".equals(resultCode)) {
                // Payment successful
                log.info("Payment successful for checkout: {}", checkoutRequestId);
                // Update payment status in database
                // updatePaymentStatus(checkoutRequestId, "COMPLETED");
            } else {
                // Payment failed
                log.warn("Payment failed for checkout: {}, reason: {}", checkoutRequestId, callbackData.get("ResultDesc"));
                // updatePaymentStatus(checkoutRequestId, "FAILED");
            }

        } catch (Exception e) {
            log.error("Error processing M-Pesa callback: {}", e.getMessage());
        }
    }

    private String getAccessToken() {
        try {
            String credentials = consumerKey + ":" + consumerSecret;
            String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

            String url = getApiUrl() + "/oauth/v1/generate?grant_type=client_credentials";
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Basic " + encodedCredentials);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> request = new HttpEntity<>(headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = response.getBody();
                if (responseBody != null && responseBody.containsKey("access_token")) {
                    return (String) responseBody.get("access_token");
                }
            }

            log.error("Failed to get access token: {}", response.getBody());
            return null;

        } catch (Exception e) {
            log.error("Error getting access token: {}", e.getMessage());
            return null;
        }
    }

    private String generatePassword(String timestamp) {
        try {
            String data = shortCode + passKey + timestamp;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            log.error("Error generating password: {}", e.getMessage());
            throw new RuntimeException("Failed to generate password", e);
        }
    }

    private String getApiUrl() {
        return "sandbox".equals(environment) 
            ? "https://sandbox.safaricom.co.ke"
            : "https://api.safaricom.co.ke";
    }
}

