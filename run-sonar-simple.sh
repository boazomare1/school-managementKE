#!/bin/bash

# Simplified SonarQube Analysis Script (Skip Tests)
echo "🔍 Running SonarQube Analysis (Code Quality Only)..."

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

# Clean and compile (skip tests)
print_status "Cleaning and compiling project (skipping tests)..."
mvn clean compile -DskipTests

if [ $? -ne 0 ]; then
    print_error "Compilation failed"
    exit 1
fi

print_success "Compilation successful"

# Run SonarQube analysis (skip tests and coverage)
print_status "Running SonarQube analysis (code quality only)..."
print_warning "Note: You need to create a project in SonarQube first!"
print_warning "1. Go to http://localhost:9000"
print_warning "2. Login with admin/admin"
print_warning "3. Create project with key: school-management-ke"
print_warning "4. Generate a token and use it below"

# Check if token is provided
if [ -z "$SONAR_TOKEN" ]; then
    print_error "SONAR_TOKEN not set. Please set it or use the web interface."
    print_status "You can also run: export SONAR_TOKEN=token"
    exit 1
fi

mvn sonar:sonar \
    -Dsonar.host.url=http://localhost:9000 \
    -Dsonar.token=$SONAR_TOKEN \
    -Dsonar.skipTests=true \
    -Dsonar.coverage.jacoco.xmlReportPaths= \
    -Dsonar.junit.reportPaths=

if [ $? -eq 0 ]; then
    print_success "SonarQube analysis completed successfully!"
    echo ""
    echo "📊 View your analysis results at: http://localhost:9000"
    echo "🔍 Project key: school-management-ke"
    echo ""
    echo "📋 Quality Metrics to Check:"
    echo "   - Code Duplication (aim for <3%)"
    echo "   - Maintainability Rating (A)"
    echo "   - Reliability Rating (A)"
    echo "   - Security Rating (A)"
    echo "   - Technical Debt (aim for <1 hour)"
    echo "   - Code Smells (minimize)"
    echo "   - Security Vulnerabilities (0)"
else
    print_error "SonarQube analysis failed"
    exit 1
fi
