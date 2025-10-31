#!/bin/bash

# Test M-Pesa STK Push for 5 KES
# This script tests STK Push with your phone number

echo "=========================================="
echo "M-Pesa STK Push Test - 5 KES"
echo "=========================================="
echo ""

# Configuration
PHONE_NUMBER="0742356449"  # Your phone number
AMOUNT="5"                  # 5 KES
BASE_URL="http://localhost:8081"

# You'll need to replace this with a valid JWT token after logging in
# First, get your JWT token by logging in:
# curl -X POST http://localhost:8081/api/auth/login \
#   -H "Content-Type: application/json" \
#   -d '{"usernameOrEmail":"your_username","password":"your_password"}'

echo "Phone Number: $PHONE_NUMBER (will be formatted to 254742356449)"
echo "Amount: KES $AMOUNT"
echo ""
echo "⚠️  NOTE: You'll need a valid JWT token to authenticate."
echo "   First, login to get your token, then use it in the Authorization header."
echo ""
echo "Testing STK Push..."
echo ""

# Test payload
cat <<EOF
Request Body:
{
  "invoiceId": 1,
  "amount": $AMOUNT,
  "paymentMethod": "M_PESA",
  "phoneNumber": "$PHONE_NUMBER",
  "accountReference": "TEST_PAYMENT_001",
  "transactionDescription": "Test payment of 5 KES"
}
EOF

echo ""
echo "=========================================="
echo "CURL Command (replace YOUR_JWT_TOKEN):"
echo "=========================================="
echo ""
cat <<EOF
curl -X POST $BASE_URL/api/finance/payments/mpesa/stk-push \\
  -H "Content-Type: application/json" \\
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \\
  -d '{
    "invoiceId": 1,
    "amount": $AMOUNT,
    "paymentMethod": "M_PESA",
    "phoneNumber": "$PHONE_NUMBER",
    "accountReference": "TEST_PAYMENT_001",
    "transactionDescription": "Test payment of 5 KES"
  }'
EOF

echo ""
echo ""
echo "After running this, you should receive an STK Push prompt on your phone ($PHONE_NUMBER)"
echo "Enter your M-Pesa PIN when prompted."
echo ""

