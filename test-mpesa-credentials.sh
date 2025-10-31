#!/bin/bash

# M-Pesa Credentials Test Script
# This tests if your Consumer Key and Secret work (without requiring Pass Key)

echo "=========================================="
echo "M-Pesa Credentials Test"
echo "=========================================="
echo ""

# Check if credentials are set
if [ -z "$MPESA_CONSUMER_KEY" ] || [ -z "$MPESA_CONSUMER_SECRET" ]; then
    echo "❌ Credentials not found in environment variables"
    echo ""
    echo "Please set them first:"
    echo "  export MPESA_CONSUMER_KEY=\"your_key\""
    echo "  export MPESA_CONSUMER_SECRET=\"your_secret\""
    echo ""
    exit 1
fi

echo "✅ Found credentials in environment"
echo ""
echo "Testing access token generation..."
echo ""

# Base64 encode credentials
CREDENTIALS="$MPESA_CONSUMER_KEY:$MPESA_CONSUMER_SECRET"
ENCODED_CREDENTIALS=$(echo -n "$CREDENTIALS" | base64)

echo "Making request to Safaricom Sandbox..."
echo ""

# Make request to get access token
RESPONSE=$(curl -s -X GET \
  "https://sandbox.safaricom.co.ke/oauth/v1/generate?grant_type=client_credentials" \
  -H "Authorization: Basic $ENCODED_CREDENTIALS" \
  -H "Content-Type: application/json")

echo "Response:"
echo "$RESPONSE" | jq . 2>/dev/null || echo "$RESPONSE"
echo ""

# Check if access token is in response
if echo "$RESPONSE" | grep -q "access_token"; then
    ACCESS_TOKEN=$(echo "$RESPONSE" | grep -o '"access_token":"[^"]*"' | cut -d'"' -f4)
    echo "✅ SUCCESS! Your credentials work!"
    echo "   Access Token received: ${ACCESS_TOKEN:0:20}..."
    echo ""
    echo "Next step: Find your Pass Key to complete STK Push setup"
    echo "   See: MPESA_PASSKEY_SOLUTION.md"
else
    echo "❌ FAILED! Could not get access token"
    echo ""
    echo "Possible issues:"
    echo "  1. Consumer Key or Secret might be incorrect"
    echo "  2. Check for extra spaces in your credentials"
    echo "  3. IP address might not be whitelisted"
    echo "  4. Network connectivity issue"
    echo ""
    echo "Full error:"
    echo "$RESPONSE"
fi

echo ""

