#!/bin/bash

# Script to create invoices for students
# Usage: ./create-invoices.sh

BASE_URL="http://localhost:8081"

echo "=========================================="
echo "Creating Invoices for Students"
echo "=========================================="
echo ""

# Check if application is running
if ! curl -s "$BASE_URL/actuator/health" > /dev/null 2>&1; then
    echo "❌ Application is not running!"
    echo "   Start it with: mvn spring-boot:run"
    exit 1
fi

echo "✅ Application is running"
echo ""

# Get JWT token
echo "Please login to get JWT token..."
read -p "Username: " USERNAME
read -sp "Password: " PASSWORD
echo ""

echo "Logging in..."
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/api/auth/login" \
  -H "Content-Type: application/json" \
  -d "{\"usernameOrEmail\":\"$USERNAME\",\"password\":\"$PASSWORD\"}")

JWT_TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"accessToken":"[^"]*"' | cut -d'"' -f4)

if [ -z "$JWT_TOKEN" ]; then
    echo "❌ Login failed!"
    echo "Response: $LOGIN_RESPONSE"
    exit 1
fi

echo "✅ Login successful"
echo ""

# You need to provide fee structure IDs
read -p "Enter Fee Structure ID to use for invoices: " FEE_STRUCTURE_ID

echo ""
echo "Creating 3 invoices for Student 1..."
for i in {1..3}; do
    echo "Creating invoice $i/3 for student 1..."
    RESPONSE=$(curl -s -X POST "$BASE_URL/api/finance/invoices?studentId=1&feeStructureId=$FEE_STRUCTURE_ID" \
      -H "Authorization: Bearer $JWT_TOKEN")
    
    if echo "$RESPONSE" | grep -q '"success":true'; then
        INVOICE_NUM=$(echo "$RESPONSE" | grep -o '"invoiceNumber":"[^"]*"' | cut -d'"' -f4)
        echo "  ✅ Invoice $i created: $INVOICE_NUM"
    else
        echo "  ❌ Failed: $RESPONSE"
    fi
done

echo ""
echo "Creating 3 invoices for Student 30..."
for i in {1..3}; do
    echo "Creating invoice $i/3 for student 30..."
    RESPONSE=$(curl -s -X POST "$BASE_URL/api/finance/invoices?studentId=30&feeStructureId=$FEE_STRUCTURE_ID" \
      -H "Authorization: Bearer $JWT_TOKEN")
    
    if echo "$RESPONSE" | grep -q '"success":true'; then
        INVOICE_NUM=$(echo "$RESPONSE" | grep -o '"invoiceNumber":"[^"]*"' | cut -d'"' -f4)
        echo "  ✅ Invoice $i created: $INVOICE_NUM"
    else
        echo "  ❌ Failed: $RESPONSE"
    fi
done

echo ""
echo "✅ Done! Created invoices for students 1 and 30"
echo ""
echo "To view invoices:"
echo "  Student 1: GET $BASE_URL/api/finance/invoices/student/1"
echo "  Student 30: GET $BASE_URL/api/finance/invoices/student/30"

