#!/bin/bash

# SonarQube Setup Script for School Management System
echo "🔍 Setting up SonarQube for School Management System..."

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

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    print_error "Docker is not installed. Please install Docker first."
    exit 1
fi

# Check if Docker Compose is installed
if ! command -v docker-compose &> /dev/null; then
    print_error "Docker Compose is not installed. Please install Docker Compose first."
    exit 1
fi

print_status "Starting SonarQube server..."
docker-compose -f docker-compose.sonar.yml up -d sonarqube

# Wait for SonarQube to start
print_status "Waiting for SonarQube to start (this may take a few minutes)..."
timeout=300
counter=0
while ! curl -s http://localhost:9000/api/system/status > /dev/null 2>&1; do
    if [ $counter -ge $timeout ]; then
        print_error "SonarQube failed to start within 5 minutes"
        exit 1
    fi
    sleep 5
    counter=$((counter + 5))
    echo -n "."
done

print_success "SonarQube is running at http://localhost:9000"

# Display login information
echo ""
print_status "SonarQube Setup Complete!"
echo ""
echo "📋 Next Steps:"
echo "1. Open your browser and go to: http://localhost:9000"
echo "2. Login with default credentials:"
echo "   - Username: admin"
echo "   - Password: admin"
echo "3. Change the default password when prompted"
echo "4. Create a new project with key: school-management-ke"
echo "5. Generate a token for the project"
echo ""
echo "🔧 To run SonarQube analysis:"
echo "   mvn clean compile test jacoco:report sonar:sonar"
echo ""
echo "🛑 To stop SonarQube:"
echo "   docker-compose -f docker-compose.sonar.yml down"
echo ""
print_success "SonarQube setup completed successfully!"
