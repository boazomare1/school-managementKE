#!/bin/bash

# SonarQube Analysis Script for School Management System
echo "🔍 Running SonarQube Analysis for School Management System..."

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if SonarQube is running
print_status "Checking if SonarQube is running..."
if ! curl -s http://localhost:9000/api/system/status > /dev/null 2>&1; then
    print_error "SonarQube is not running. Please start it first with: ./setup-sonar.sh"
    exit 1
fi

print_success "SonarQube is running"

# Clean and compile
print_status "Cleaning and compiling project..."
mvn clean compile

if [ $? -ne 0 ]; then
    print_error "Compilation failed"
    exit 1
fi

print_success "Compilation successful"

# Run tests with coverage
print_status "Running tests with coverage..."
mvn test jacoco:report

if [ $? -ne 0 ]; then
    print_warning "Some tests failed, but continuing with analysis..."
fi

# Run SonarQube analysis
print_status "Running SonarQube analysis..."
mvn sonar:sonar \
    -Dsonar.host.url=http://localhost:9000 \
    -Dsonar.login=admin \
    -Dsonar.password=admin

if [ $? -eq 0 ]; then
    print_success "SonarQube analysis completed successfully!"
    echo ""
    echo "📊 View your analysis results at: http://localhost:9000"
    echo "🔍 Project key: school-management-ke"
    echo ""
    echo "📋 Quality Metrics to Check:"
    echo "   - Code Coverage (aim for >80%)"
    echo "   - Code Duplication (aim for <3%)"
    echo "   - Maintainability Rating (A)"
    echo "   - Reliability Rating (A)"
    echo "   - Security Rating (A)"
    echo "   - Technical Debt (aim for <1 hour)"
else
    print_error "SonarQube analysis failed"
    exit 1
fi
