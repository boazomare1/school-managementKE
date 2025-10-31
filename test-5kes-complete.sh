#!/bin/bash

# Complete test script for 5 KES payment
# This script will help you test the M-Pesa STK Push

echo "=========================================="
echo "M-Pesa STK Push Test - 5 KES to 0742356449"
echo "=========================================="
echo ""

BASE_URL="http://localhost:8081"

# Check if application is running
echo "1. Checking if application is running..."
if ! curl -s "$BASE_URL/actuator/health" > /dev/null 2>&1; then
    echo "❌ Application is not running!"
    echo "   Start it with: mvn spring-boot:run"
    exit 1
fi
echo "✅ Application is running"
echo ""

# Step 1: Login
echo "2. Login required to get JWT token"
echo ""
echo "Please enter your login credentials:"
read -p "Username or Email: " USERNAME
read -sp "Password: " PASSWORD
echo ""

echo "Logging in..."
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/api/auth/login" \
  -H "Content-Type: application/json" \
  -d "{\"usernameOrEmail\":\"$USERNAME\",\"password\":\"$PASSWORD\"}")

# Extract token
JWT_TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"accessToken":"[^"]*"' | cut -d'"' -f4)

if [ -z "$JWT_TOKEN" ]; then
    echo "❌ Login failed!"
    echo "Response: $LOGIN_RESPONSE"
    exit 1
fi

echo "✅ Login successful"
echo ""

# Step 2: Initiate STK Push
echo "3. Initiating M-Pesa STK Push for 5 KES to 0742356449..."
echo ""

STK_RESPONSE=$(curl -s -X POST "$BASE_URL/api/finance/payments/mpesa/stk-push" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -d '{
    "invoiceId": 1,
    "amount": 5,
    "paymentMethod": "M_PESA",
    "phoneNumber": "0742356449",
    "accountReference": "TEST_5KES",
    "transactionDescription": "Test payment of 5 KES"
  }')

echo "Response:"
echo "$STK_RESPONSE" | jq . 2>/dev/null || echo "$STK_RESPONSE"
echo ""

# Extract checkout request ID
CHECKOUT_ID=$(echo "$STK_RESPONSE" | grep -o '"checkoutRequestId":"[^"]*"' | cut -d'"' -f4)

if [ -n "$CHECKOUT_ID" ]; then
    echo "✅ SUCCESS! STK Push initiated"
    echo ""
    echo "📱 Check your phone (0742356449) for the M-Pesa prompt"
    echo "   - You'll see a popup on your phone"
    echo "   - Enter your M-Pesa PIN"
    echo "   - Confirm the payment of 5 KES"
    echo ""
    echo "Checkout Request ID: $CHECKOUT_ID"
    echo ""
    echo "To check payment status later:"
    echo "  curl $BASE_URL/api/payments/webhooks/mpesa/status/$CHECKOUT_ID"
else
    echo "❌ Failed to initiate STK Push"
    echo ""
    echo "Check the error message above"
fi

echo ""

