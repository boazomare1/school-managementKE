# 🔍 SonarQube Setup Guide for School Management System

## Overview
This guide will help you set up SonarQube for code quality analysis of your Spring Boot school management system.

## Prerequisites
- Docker and Docker Compose installed
- Java 17+
- Maven 3.6+

## Step 1: Start SonarQube Server

```bash
# Start SonarQube using Docker
./setup-sonar.sh
```

This will:
- Start SonarQube server on http://localhost:9000
- Create necessary volumes for data persistence
- Wait for SonarQube to be ready

## Step 2: Initial SonarQube Configuration

1. **Open SonarQube in your browser**: http://localhost:9000

2. **Login with default credentials**:
   - Username: `admin`
   - Password: `admin`

3. **Change the default password** when prompted

4. **Create a new project**:
   - Click "Create Project"
   - Choose "Manually"
   - Project key: `school-management-ke`
   - Display name: `School Management System - Kenya`

5. **Generate a token**:
   - Go to "My Account" → "Security"
   - Generate a new token
   - Copy the token (you'll need it for analysis)

## Step 3: Run Code Quality Analysis

### Option A: Simple Analysis (Recommended for first run)
```bash
# Run analysis without tests
./run-sonar-simple.sh
```

### Option B: Full Analysis (with tests and coverage)
```bash
# Run full analysis (requires working tests)
./run-sonar-analysis.sh
```

## Step 4: View Results

1. **Open SonarQube**: http://localhost:9000
2. **Navigate to your project**: `school-management-ke`
3. **Review quality metrics**:
   - Code Coverage
   - Code Duplication
   - Maintainability Rating
   - Reliability Rating
   - Security Rating
   - Technical Debt

## Quality Metrics to Monitor

### Target Values
- **Code Coverage**: >80%
- **Code Duplication**: <3%
- **Maintainability Rating**: A
- **Reliability Rating**: A
- **Security Rating**: A
- **Technical Debt**: <1 hour
- **Code Smells**: Minimize
- **Security Vulnerabilities**: 0

### Common Issues to Fix

1. **@Builder Warnings**: Add `@Builder.Default` to fields with initial values
2. **Code Duplication**: Extract common code into methods
3. **Long Methods**: Break down into smaller methods
4. **Complex Methods**: Reduce cyclomatic complexity
5. **Unused Imports**: Remove unused imports
6. **Magic Numbers**: Use constants instead of hardcoded values

## Step 5: Configure Quality Gates

1. **Go to Quality Gates**: Administration → Quality Gates
2. **Create a new Quality Gate** for your project
3. **Set conditions**:
   - Coverage > 80%
   - Duplicated Lines < 3%
   - Maintainability Rating = A
   - Reliability Rating = A
   - Security Rating = A

## Step 6: Integrate with CI/CD

### GitHub Actions Example
```yaml
name: SonarQube Analysis
on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

jobs:
  sonarqube:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v3
    - name: SonarQube Scan
      uses: sonarqube-quality-gate-action@master
      env:
        SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
        SONAR_HOST_URL: ${{ secrets.SONAR_HOST_URL }}
```

## Troubleshooting

### Common Issues

1. **"Not authorized" error**:
   - Make sure you've created a project in SonarQube
   - Generate a token and use it in the analysis

2. **SonarQube not starting**:
   - Check Docker is running
   - Check port 9000 is not in use
   - Check Docker logs: `docker logs sonarqube`

3. **Analysis fails**:
   - Check SonarQube server is running
   - Verify project key matches
   - Check authentication token

### Useful Commands

```bash
# Check SonarQube status
curl http://localhost:9000/api/system/status

# View SonarQube logs
docker logs sonarqube

# Stop SonarQube
docker-compose -f docker-compose.sonar.yml down

# Restart SonarQube
docker-compose -f docker-compose.sonar.yml restart
```

## Best Practices

1. **Run analysis regularly**: Integrate into your development workflow
2. **Fix issues early**: Address quality issues as they arise
3. **Set up quality gates**: Prevent merging of low-quality code
4. **Monitor trends**: Track quality metrics over time
5. **Team training**: Educate team on SonarQube rules and best practices

## Advanced Configuration

### Custom Rules
- Go to Administration → Rules
- Create custom rules for your project
- Set up rule templates

### Quality Profiles
- Go to Administration → Quality Profiles
- Create project-specific quality profiles
- Configure rule severities

### Project Administration
- Set up project-specific settings
- Configure exclusions
- Set up notifications

## Resources

- [SonarQube Documentation](https://docs.sonarqube.org/)
- [SonarQube Rules](https://rules.sonarsource.com/)
- [Quality Gates](https://docs.sonarqube.org/latest/user-guide/quality-gates/)
- [SonarQube API](https://docs.sonarqube.org/latest/extend/web-api/)

## Support

If you encounter issues:
1. Check the troubleshooting section above
2. Review SonarQube logs
3. Consult the official documentation
4. Check the project's GitHub issues
