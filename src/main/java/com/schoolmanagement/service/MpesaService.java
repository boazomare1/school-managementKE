package com.schoolmanagement.service;

import com.schoolmanagement.entity.Payment;
import com.schoolmanagement.entity.PaymentGatewayConfig;
import com.schoolmanagement.repository.PaymentGatewayConfigRepository;
import com.schoolmanagement.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class MpesaService {

    private final RestTemplate restTemplate;
    
    public MpesaService() {
        // Configure RestTemplate with browser-like headers to bypass Incapsula protection
        ClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        restTemplate = new RestTemplate(factory);
        
        // Add interceptor to add browser-like headers to all requests
        restTemplate.getInterceptors().add((request, body, execution) -> {
            HttpHeaders headers = request.getHeaders();
            // Add browser-like headers to bypass bot protection
            headers.add("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
            headers.add("Accept", "application/json, text/plain, */*");
            headers.add("Accept-Language", "en-US,en;q=0.9");
            headers.add("Accept-Encoding", "gzip, deflate, br");
            headers.add("Connection", "keep-alive");
            headers.add("Cache-Control", "no-cache");
            return execution.execute(request, body);
        });
    }

    @Value("${mpesa.consumer-key:17ZdF9Q2PKAOwJ64Yzl1dxS4iZMsQOAXUUaEVSQZrTmDbMkG}")
    private String consumerKey;

    @Value("${mpesa.consumer-secret:ZGhNJhzbE9DHKKmAhAZWI045A5B6yUG3uAqnXpx5LJjXYWH7Stn8xUxqGHWpfhKE}")
    private String consumerSecret;

    @Value("${mpesa.short-code:174379}")
    private String shortCode;

    @Value("${mpesa.pass-key:bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919}")
    private String passKey;

    @Value("${mpesa.callback-url:http://localhost:8081/api/finance/payments/webhooks/mpesa}")
    private String callbackUrl;

    @Value("${mpesa.environment:sandbox}")
    private String environment;

    @jakarta.annotation.PostConstruct
    public void init() {
        log.info("M-Pesa Configuration loaded - Environment: {}, ShortCode: {}, ConsumerKey: {}...{}, PassKey: {}...{}, CallbackURL: {}", 
            environment != null ? environment.toUpperCase() : "NULL",
            shortCode != null ? shortCode : "NULL", 
            consumerKey != null && consumerKey.length() > 10 ? consumerKey.substring(0, 10) : "NULL",
            consumerKey != null && consumerKey.length() > 10 ? "***" : "",
            passKey != null && passKey.length() > 10 ? passKey.substring(0, 10) : "NULL",
            passKey != null && passKey.length() > 10 ? "***" : "",
            callbackUrl != null ? callbackUrl : "NULL");
        
        if (!"sandbox".equalsIgnoreCase(environment) && !"production".equalsIgnoreCase(environment)) {
            log.warn("Invalid M-Pesa environment '{}'. Expected 'sandbox' or 'production'. Defaulting to sandbox.", environment);
        }
    }

    public String initiateSTKPush(String phoneNumber, String amount, String accountReference, String transactionDescription) {
        try {
            // Format phone number to 254XXXXXXXXX format
            String formattedPhone = formatPhoneNumber(phoneNumber);
            log.info("Initiating M-Pesa STK Push for phone: {} (formatted: {}), amount: {}", phoneNumber, formattedPhone, amount);
            log.debug("M-Pesa config check - shortCode: '{}', passKey: '{}'", shortCode, passKey != null ? "***" : "NULL");

            // Validate required fields
            if (formattedPhone == null || formattedPhone.length() != 12) {
                throw new IllegalArgumentException("Invalid phone number format. Expected: 254XXXXXXXXX");
            }
            
            log.info("VALIDATION CHECK - shortCode value: '{}', isNull: {}, isEmpty: {}", 
                shortCode, shortCode == null, shortCode != null && shortCode.isEmpty());
            
            if (shortCode == null || shortCode.isEmpty()) {
                log.error("ShortCode validation failed - value: '{}', consumerKey: '{}', passKey: '{}'", 
                    shortCode, consumerKey != null ? consumerKey.substring(0, 10) + "..." : "NULL",
                    passKey != null ? "***" : "NULL");
                throw new IllegalArgumentException("M-Pesa short code not configured");
            }
            if (passKey == null || passKey.isEmpty()) {
                log.error("PassKey validation failed - value: '{}'", passKey != null ? "***" : "NULL");
                throw new IllegalArgumentException("M-Pesa pass key not configured");
            }

            // Get access token
            String accessToken = getAccessToken();
            if (accessToken == null) {
                throw new RuntimeException("Failed to get access token");
            }

            // Prepare STK Push request
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String password = generatePassword(timestamp);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("BusinessShortCode", shortCode);
            requestBody.put("Password", password);
            requestBody.put("Timestamp", timestamp);
            requestBody.put("TransactionType", "CustomerPayBillOnline");
            requestBody.put("Amount", amount);
            requestBody.put("PartyA", formattedPhone);
            requestBody.put("PartyB", shortCode);
            requestBody.put("PhoneNumber", formattedPhone);
            requestBody.put("CallBackURL", callbackUrl);
            requestBody.put("AccountReference", accountReference != null ? accountReference : "PAYMENT");
            requestBody.put("TransactionDesc", transactionDescription != null ? transactionDescription : "Payment");

            // Make API call
            String url = getApiUrl() + "/mpesa/stkpush/v1/processrequest";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);
            // Additional headers for Incapsula bypass (already added by interceptor, but ensure they're present)
            headers.set("Origin", getApiUrl());
            headers.set("Referer", getApiUrl() + "/");

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

            String resultCode = callbackData.get("ResultCode") != null 
                ? callbackData.get("ResultCode").toString() 
                : null;
            String checkoutRequestId = (String) callbackData.get("CheckoutRequestID");

            log.info("Callback processed - ResultCode: {}, CheckoutRequestID: {}", resultCode, checkoutRequestId);

            if ("0".equals(resultCode)) {
                // Payment successful
                log.info("Payment successful for checkout: {}", checkoutRequestId);
            } else {
                // Payment failed
                log.warn("Payment failed for checkout: {}, reason: {}", checkoutRequestId, callbackData.get("ResultDesc"));
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
            // Additional headers to help bypass Incapsula
            headers.set("Origin", getApiUrl());
            headers.set("Referer", getApiUrl() + "/");

            HttpEntity<String> request = new HttpEntity<>(headers);
            log.info("Requesting access token from: {}", url);
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, request, Map.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = response.getBody();
                if (responseBody != null && responseBody.containsKey("access_token")) {
                    String token = (String) responseBody.get("access_token");
                    log.info("Successfully obtained M-Pesa access token");
                    return token;
                }
            }

            log.error("Failed to get access token - Status: {}, Body: {}", response.getStatusCode(), response.getBody());
            return null;

        } catch (HttpClientErrorException e) {
            log.error("HTTP error getting access token - Status: {}, Body: {}", e.getStatusCode(), e.getResponseBodyAsString());
            return null;
        } catch (Exception e) {
            log.error("Error getting access token: {}", e.getMessage(), e);
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

    /**
     * Format phone number to M-Pesa format (254XXXXXXXXX)
     * Accepts formats: 254XXXXXXXXX, 07XXXXXXXX, +254XXXXXXXXX
     */
    private String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return null;
        }

        // Remove spaces and special characters
        String cleaned = phoneNumber.replaceAll("[\\s\\-()]+", "");

        // Handle different formats
        if (cleaned.startsWith("+254")) {
            return cleaned.substring(1); // Remove +
        } else if (cleaned.startsWith("254")) {
            return cleaned;
        } else if (cleaned.startsWith("0")) {
            return "254" + cleaned.substring(1); // Replace 0 with 254
        } else if (cleaned.length() == 9) {
            return "254" + cleaned; // Add 254 prefix
        } else if (cleaned.length() == 12 && cleaned.startsWith("254")) {
            return cleaned;
        }

        log.warn("Unable to format phone number: {}", phoneNumber);
        return cleaned; // Return as-is and let API validate
    }
}

