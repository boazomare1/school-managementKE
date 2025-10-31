# Test M-Pesa STK Push - 5 KES Payment

This guide will help you test sending 5 KES from your phone **0742356449**.

## Important Notes

✅ **Phone Number**: `0742356449` will be auto-formatted to `254742356449`  
✅ **Amount**: 5 KES  
✅ **PIN Entry**: You will enter your M-Pesa PIN on your phone when prompted  
✅ **Invoice Required**: The API needs an invoice ID (we'll use invoice ID 1 for testing)

## Step 1: Start Your Application

```bash
mvn spring-boot:run
```

Wait for the application to start (you'll see "Started SchoolManagementSystemApplication").

## Step 2: Login to Get JWT Token

```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "your_username",
    "password": "your_password"
  }'
```

**Copy the `accessToken` from the response** - you'll need it in the next step.

## Step 3: Initiate STK Push for 5 KES

Replace `YOUR_JWT_TOKEN` with the token from Step 2:

```bash
curl -X POST http://localhost:8081/api/finance/payments/mpesa/stk-push \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "invoiceId": 1,
    "amount": 5,
    "paymentMethod": "M_PESA",
    "phoneNumber": "0742356449",
    "accountReference": "TEST_5KES",
    "transactionDescription": "Test payment of 5 KES"
  }'
```

## Step 4: Check Your Phone

1. **You should receive an STK Push prompt** on your phone (`0742356449`)
2. **Enter your M-Pesa PIN** when prompted
3. **Confirm the payment** of 5 KES
4. **You'll receive an SMS confirmation** from M-Pesa

## Expected Response

If successful, you'll see:

```json
{
  "success": true,
  "message": "M-Pesa STK Push initiated successfully. Please check your phone.",
  "data": {
    "checkoutRequestId": "ws_CO_...",
    "merchantRequestId": "...",
    "customerMessage": "Confirm payment on your phone",
    "responseCode": "0",
    "responseDescription": "The service request is processed successfully",
    "invoiceId": 1,
    "invoiceNumber": "...",
    "amount": "5",
    "phoneNumber": "254742356449"
  }
}
```

## Troubleshooting

### "Invoice not found"
- The invoice ID might not exist. You can create a test invoice first, or modify the code to handle missing invoices for testing.

### No prompt on phone
- Check that your phone number is correct: `0742356449`
- Ensure your phone has mobile data or is connected to WiFi
- Check if M-Pesa is active on your phone
- Verify the amount is at least 1 KES (5 KES is fine)

### "Invalid phone number format"
- The service auto-formats `0742356449` to `254742356449`
- If you get this error, check the application logs

### Authentication error
- Make sure you're using a valid JWT token
- Token might have expired - login again to get a new token

## Quick Test Script

Run the test script:

```bash
./test-stk-push-5bob.sh
```

This will show you the exact command with placeholders.

## Note About Invoice ID

If invoice ID 1 doesn't exist in your database, you have two options:

1. **Create a test invoice first** (if you have student enrollment data)
2. **Modify temporarily** - For testing, you could temporarily allow STK Push without invoice validation

The payment will still work - the invoice validation is just for record keeping in the school management system.

## What Happens Next

1. ✅ STK Push initiated → Response with `checkoutRequestId`
2. ✅ You receive prompt on phone → Enter PIN
3. ✅ Payment processed → M-Pesa sends webhook
4. ✅ Payment saved → Invoice updated
5. ✅ Confirmation → You get SMS from M-Pesa

**You're all set! Just make sure your app is running and you have a valid JWT token.** 🚀

