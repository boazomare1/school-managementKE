#!/bin/bash

# SonarQube Quick Start Script
echo "🔍 SonarQube Quick Start for School Management System"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

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

echo ""
print_status "🚀 Starting SonarQube Quick Start Process..."
echo ""

# Step 1: Start SonarQube
print_status "Step 1: Starting SonarQube server..."
./setup-sonar.sh

if [ $? -ne 0 ]; then
    print_error "Failed to start SonarQube"
    exit 1
fi

echo ""
print_status "Step 2: Manual Configuration Required"
echo ""
print_warning "Please complete these steps in your browser:"
echo ""
echo "1. 🌐 Open http://localhost:9000 in your browser"
echo "2. 🔐 Login with:"
echo "   - Username: admin"
echo "   - Password: admin"
echo "3. 🔄 Change the default password when prompted"
echo "4. 📁 Create a new project:"
echo "   - Click 'Create Project'"
echo "   - Choose 'Manually'"
echo "   - Project key: school-management-ke"
echo "   - Display name: School Management System - Kenya"
echo "5. 🔑 Generate a token:"
echo "   - Go to 'My Account' → 'Security'"
echo "   - Generate a new token"
echo "   - Copy the token"
echo ""
print_warning "Press Enter when you've completed these steps..."
read -r

echo ""
print_status "Step 3: Running Code Quality Analysis"
echo ""
print_warning "Please set your SonarQube token:"
echo "export SONAR_TOKEN=sqa_2a8e0f458015998e035ee09f78eeabc98b81de58"
echo ""
print_warning "Then run: ./run-sonar-simple.sh"
echo ""

print_success "SonarQube setup completed!"
echo ""
echo "📋 Next Steps:"
echo "1. Set your token: export SONAR_TOKEN=sqa_2a8e0f458015998e035ee09f78eeabc98b81de58"
echo "2. Run analysis: ./run-sonar-simple.sh"
echo "3. View results: http://localhost:9000"
echo ""
echo "📚 For detailed instructions, see: SONARQUBE_SETUP_GUIDE.md"
