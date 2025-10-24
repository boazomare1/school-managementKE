# 🎓 **School Management System - Complete Implementation**

## 📋 **Project Overview**

A comprehensive Spring Boot 3.3.4 backend system for school management with JWT authentication, role-based authorization, email notifications, and complete student lifecycle management. Built with Java 17, Maven, PostgreSQL, and production-ready features.

## 🚀 **Key Features Implemented**

### ✅ **Authentication & Security**
- JWT-based authentication with refresh token support
- Role-based authorization (ADMIN, STUDENT, TEACHER, PARENT, HR, FINANCE)
- BCrypt password hashing
- Spring Security configuration
- CORS support

### ✅ **User Management System**
- Complete user CRUD operations (`UserController`)
- Paginated user listing with search and role filtering
- User activation/deactivation
- Role-based user queries
- User profile management
- Security checks for user operations

### ✅ **Library Management System**
- Complete library book management (`LibraryController`)
- Book CRUD operations with validation
- Advanced search functionality (title, author, category, subject)
- Book borrowing and return system
- Inventory tracking (available/borrowed copies)
- Book status management (AVAILABLE, BORROWED, RESERVED, DAMAGED, LOST)
- Category and subject-based filtering
- Paginated book listings

### ✅ **Email Notification System**
- Gmail SMTP integration with working authentication
- HTML email templates (Thymeleaf)
- Automated email reminders and notifications
- Email simulation endpoints for testing
- Templates for: class reminders, exam reminders, fee reminders, attendance notifications, grade notifications

### ✅ **Student Lifecycle Management**
- Complete student enrollment workflow
- Parent/guardian relationship management (max 2 parents + 1 additional contact)
- Student class and dormitory assignments
- Academic progress tracking
- Fee payment integration
- Dining access control (biometric authentication simulation)

### ✅ **Teacher Management**
- Teacher specialization system (Primary, Secondary, Optional, Interest roles)
- Subject portfolio management
- Teacher workload tracking
- Class teacher assignments
- Dormitory master assignments
- Complete teacher profile system with role assignments
- Teacher specializations for academic and interest-based subjects
- Timetable-ready teacher assignments

### ✅ **Academic Management**
- School timetable system with conflict detection
- Subject management (8-4-4 and CBC/CBE curriculum support)
- Class and stream management
- Attendance tracking
- Exam and grade management
- Lesson planning

### ✅ **Finance Module**
- **Complete Fee Management System** - Fee structure creation, management, and tracking
- **Advanced Payment Processing** - Multiple payment methods (Cash, M-Pesa, Stripe, Bank Transfer)
- **Invoice Generation & Management** - Automated invoice creation with due date tracking
- **Payment Gateway Integration** - M-Pesa STK Push and Stripe payment intents
- **Comprehensive Reporting** - Finance dashboard, fee summaries, payment analytics
- **Kenya-Specific Features** - Capitation grants, bursary eligibility, government compliance
- **Refund Management** - Complete refund processing with audit trails
- **Webhook Support** - Real-time payment notifications from M-Pesa and Stripe
- **Overdue Fee Tracking** - Automated identification and reminder systems

### ✅ **Dynamic Payroll Management System**
- **Flexible Payroll Items** - Support for both fixed amounts and percentage-based calculations
- **Kenyan Payroll Compliance** - PAYE (10%), NSSF, NHIF, Housing Levy (1.5%), Digital Service Tax
- **Dynamic Item Management** - Add, update, or remove payroll items as regulations change
- **Percentage-Based Calculations** - Automatic PAYE and Housing Levy calculations
- **Support Staff Management** - Complete onboarding, role assignment, and payroll processing
- **Real-Time Payroll Processing** - Live calculation of allowances, deductions, and net pay
- **Government Compliance** - Statutory deductions with proper categorization
- **Future-Proof System** - Easy addition of new government deductions and company benefits

### ✅ **Automatic ID Generation System (Frappe-style)**
- **Naming Series Management** - Flexible ID generation with prefix, suffix, padding, and separators
- **Multiple Series Types** - EMPLOYEE, STUDENT, TEACHER, SUPPORT_STAFF, PARENT, ADMIN
- **Default Series** - Each type has a default series for automatic generation
- **Custom Series** - Create custom series for specific needs (CHEF, CLEANER, MATH, etc.)
- **Automatic Generation** - No more hardcoded IDs - everything generated automatically
- **Sequential Numbering** - Proper increment with customizable padding
- **Series Management** - Create, update, deactivate, and reset series counters
- **API Integration** - Seamless integration with all entity creation endpoints

### ✅ **Dormitory & Dining**
- Dormitory room management
- Student dormitory assignments
- Biometric dining access control
- Fee payment verification for meal access
- Thermal ticket generation

### ✅ **Kenya Compliance Features**
- NEMIS (National Education Management Information System) integration
- CBC (Competency-Based Curriculum) support
- TSC (Teachers Service Commission) requirements
- KNEC (Kenya National Examinations Council) formats
- Kenya Data Protection Act (2019) compliance

## 🛠 **Technical Stack**

- **Backend**: Spring Boot 3.3.4
- **Java**: Java 17
- **Build Tool**: Maven
- **Database**: PostgreSQL
- **ORM**: JPA/Hibernate
- **Security**: Spring Security + JWT
- **Email**: Spring Mail + Gmail SMTP
- **Templates**: Thymeleaf
- **Testing**: JUnit 5 + Mockito
- **Documentation**: OpenAPI/Swagger
- **Logging**: SLF4J
- **Monitoring**: Spring Actuator

## 🔧 **Recent Improvements & Fixes**

### **Automatic ID Generation System (Latest Update)**
- ✅ **NamingSeries Entity** - Frappe-style naming series with flexible ID generation
- ✅ **Multiple Series Types** - EMPLOYEE, STUDENT, TEACHER, SUPPORT_STAFF, PARENT, ADMIN
- ✅ **Default Series** - Pre-configured series for all entity types
- ✅ **Custom Series** - Create custom series (CHEF, CLEANER, MATH, etc.)
- ✅ **Automatic Generation** - No more hardcoded IDs - everything generated automatically
- ✅ **IdGenerationService** - Centralized service for all ID generation needs
- ✅ **API Integration** - Seamless integration with entity creation endpoints
- ✅ **Series Management** - Complete CRUD operations for naming series

### **Dynamic Payroll Management System**
- ✅ **PayrollItem Entity** - Flexible structure supporting fixed amounts and percentages
- ✅ **Percentage-Based PAYE** - Automatic 10% PAYE calculation on gross salary
- ✅ **Kenyan 2024 Compliance** - Housing Levy (1.5%), Digital Service Tax, updated NSSF/NHIF
- ✅ **Dynamic Item Management** - Add/update/remove payroll items via REST API
- ✅ **Support Staff Onboarding** - Complete chef onboarding with payroll processing
- ✅ **Real-Time Calculations** - Live payroll processing with accurate deductions
- ✅ **Government Compliance** - Statutory deductions with proper categorization
- ✅ **Future-Proof Design** - Easy addition of new government regulations

### **Comprehensive Finance Module**
- ✅ **Enhanced FinanceController** - Added 25+ endpoints for complete finance management
- ✅ **Advanced Payment Processing** - Multiple payment methods with gateway integration
- ✅ **Finance Dashboard** - Real-time analytics and reporting capabilities
- ✅ **Kenya-Specific Features** - Capitation grants, bursary eligibility, government compliance
- ✅ **Payment Gateway Integration** - M-Pesa STK Push and Stripe payment intents
- ✅ **Comprehensive Reporting** - Fee summaries, payment analytics, overdue tracking
- ✅ **Webhook Support** - Real-time payment notifications from external gateways
- ✅ **Updated Postman Collection** - Complete finance API testing suite

### **Complete Teacher Profile System**
- ✅ **Fixed Subject Database Schema** - Added missing `category`, `curriculum_type`, and `credits` columns
- ✅ **Created Teacher Specializations** - Added Mathematics, Chemistry, Business Studies, PHE as academic specializations
- ✅ **Added Interest-Based Roles** - Music, Film Studies, Soccer for extracurricular activities
- ✅ **Teacher Assignments** - Successfully assigned Mr Boaz Omare as class teacher for Form 3C and dorm master for Kenyatta House
- ✅ **Subject Management** - Created complete subject catalog with proper categorization
- ✅ **Database Schema Fixes** - Resolved all database constraint violations
- ✅ **Future-Ready Entities** - Created reusable dormitory and class entities for easy expansion

### **Library Management System**
- ✅ **Fixed Missing LibraryController** - Created complete library management endpoints
- ✅ **Enhanced LibraryService** - Added book management, search, and borrowing functionality
- ✅ **Updated LibraryBookRepository** - Added advanced search queries and filtering
- ✅ **Book Management Features** - CRUD operations, inventory tracking, status management
- ✅ **Search & Filtering** - Advanced search by title, author, category, subject
- ✅ **Borrowing System** - Book borrowing and return with copy tracking
- ✅ **Tested Library Endpoints** - Verified all library operations work correctly

### **User Management System**
- ✅ **Fixed Missing UserController** - Created complete user management endpoints
- ✅ **Enhanced UserService** - Added pagination, search, and role filtering capabilities
- ✅ **Updated UserRepository** - Added custom queries for role-based and search operations
- ✅ **Tested User Management** - Verified all CRUD operations work correctly
- ✅ **Security Integration** - Proper role-based access control for user operations

### **Issue Resolution Framework**
- 🔍 **User-Based Testing** - Systematic testing approach for different user roles
- 🐛 **Issue Identification** - Proactive identification of missing endpoints and functionality
- 🔧 **Rapid Fixes** - Quick resolution of critical user management issues
- 📚 **Documentation Updates** - Real-time README updates with improvements

## 📁 **Project Structure**

```
src/main/java/com/schoolmanagement/
├── config/          # Configuration classes
├── controller/      # REST API controllers
├── dto/            # Data Transfer Objects
├── entity/         # JPA entities
├── exception/      # Custom exceptions
├── repository/     # Data access layer
├── security/       # Security configuration
└── service/        # Business logic layer
```

## 🎯 **Complete Teacher Profile System**

### **Mr Boaz Omare - Fully Configured Teacher**
- **👨‍🏫 Teacher Profile**: TSC001, Mathematics Department, 5 years experience
- **🏠 Dorm Master**: Assigned to Kenyatta House (50 students, KES 5,000/month)
- **📚 Class Teacher**: Assigned to Form 3C (40 students capacity)
- **🎓 Academic Specializations**:
  - **Primary**: Mathematics, Chemistry
  - **Secondary**: Business Studies, Physical Health Education
- **🎵 Interest-Based Roles**:
  - **Music**: Music club leadership
  - **Film Studies**: Photography and media club
  - **Soccer**: Sports and athletics coaching

### **Reusable Entity System**
- **🏠 Kenyatta House Dormitory**: Ready for future dormitory creation (Moi House, Kibaki House, etc.)
- **📚 Form 3C Class**: Template for creating Form 2A, Form 1B, Form 4D, etc.
- **📖 Subject Catalog**: Complete curriculum support with proper categorization
- **👨‍🏫 Teacher Specializations**: Flexible system for academic and extracurricular roles

## 🗄 **Database Schema**

### **Core Entities**
- **User**: Authentication and user management
- **StudentEnrollment**: Student academic records
- **Teacher**: Teacher profiles and specializations
- **ClassEntity**: School classes and streams
- **Subject**: Academic subjects and curriculum
- **Timetable**: School schedule management
- **Attendance**: Student attendance tracking
- **Exam**: Examination management
- **Grade**: Academic performance tracking

### **Finance Entities**
- **FeeStructure**: Fee configuration
- **FeeInvoice**: Student billing
- **Payment**: Payment processing
- **PaymentGatewayConfig**: Gateway settings
- **Refund**: Refund management

### **Support Entities**
- **Dormitory**: Boarding facilities
- **DormitoryRoom**: Individual rooms
- **DormitoryReservation**: Student assignments
- **MealOrder**: Dining management
- **Document**: File management
- **Assignment**: Academic assignments

## 🔧 **Configuration**

### **Application Properties**
```yaml
spring:
  profiles:
    active: dev
  datasource:
    url: jdbc:postgresql://localhost:5432/school_management
    username: root
    password: Abutwalib12@#
  jpa:
    hibernate:
      ddl-auto: update
  mail:
    host: smtp.gmail.com
    port: 587
    username: koboobooko2@gmail.com
    password: wnwoemutinmqmjlf
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
```

### **Environment Variables**
- `SPRING_PROFILES_ACTIVE`: dev/prod
- `DB_USERNAME`: Database username
- `DB_PASSWORD`: Database password
- `JWT_SECRET`: JWT signing secret
- `MAIL_USERNAME`: Email username
- `MAIL_PASSWORD`: Email password

## 🚀 **Getting Started**

### **Prerequisites**
- Java 17+
- Maven 3.6+
- PostgreSQL 12+
- Gmail account with app password
- Docker (for SonarQube)
- SonarQube (for code quality checks)

### **Database Setup**
```bash
# Create database
createdb school_management

# Grant privileges to root user
psql -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE school_management TO root;"
```

### **Running the Application**
```bash
# Development
mvn spring-boot:run

# Production
mvn clean package
java -jar target/school-management-system.jar
```

### **Code Quality Setup**
```bash
# Quick start (recommended)
./sonar-quick-start.sh

# Or manual setup:
# 1. Start SonarQube server
./setup-sonar.sh

# 2. Configure project in browser (http://localhost:9000)
# 3. Set token and run analysis
export SONAR_TOKEN=your_token_here
./run-sonar-simple.sh

# View results at http://localhost:9000
```

### **Docker Deployment**
```bash
# Build image
docker build -t school-management-system .

# Run container
docker run -p 8081:8081 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DB_USERNAME=root \
  -e DB_PASSWORD=Abutwalib12@# \
  school-management-system
```

## 📡 **API Endpoints**

### **Authentication**
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login
- `POST /api/auth/refresh` - Refresh JWT token

### **Student Management**
- `POST /api/students/enroll` - Enroll new student
- `GET /api/students` - List students
- `GET /api/students/{id}` - Get student details
- `PUT /api/students/{id}` - Update student

### **Academic Management**
- `GET /api/classes` - List classes
- `GET /api/subjects` - List subjects
- `GET /api/timetable` - Get timetable
- `POST /api/attendance` - Mark attendance
- `POST /api/exams` - Create exam

### **Finance Management**
- `GET /api/fees/structures` - Fee structures
- `POST /api/payments/process` - Process payment
- `GET /api/payments/history` - Payment history
- `POST /api/mpesa/stk-push` - M-Pesa payment

### **Payroll Management**
- `GET /api/payroll-items` - Get all payroll items
- `GET /api/payroll-items/type/{type}` - Get items by type (ALLOWANCE/DEDUCTION)
- `GET /api/payroll-items/mandatory` - Get mandatory items
- `POST /api/payroll-items` - Create new payroll item
- `PUT /api/payroll-items/{id}` - Update payroll item
- `DELETE /api/payroll-items/{id}` - Deactivate payroll item
- `GET /api/payroll-items/search?q={term}` - Search payroll items
- `POST /api/payroll` - Create payroll record
- `POST /api/payroll/allowances` - Add allowance to payroll
- `POST /api/payroll/deductions` - Add deduction to payroll
- `GET /api/payroll/{id}` - Get payroll details
- `GET /api/payroll/staff/{staffId}` - Get staff payroll history

### **Support Staff Management**
- `POST /api/support-staff` - Create support staff profile
- `GET /api/support-staff` - List support staff
- `GET /api/support-staff/{id}` - Get staff details
- `PUT /api/support-staff/{id}` - Update staff profile
- `DELETE /api/support-staff/{id}` - Deactivate staff

### **Automatic ID Generation**
- `GET /api/naming-series` - Get all naming series
- `GET /api/naming-series/type/{type}` - Get series by type
- `GET /api/naming-series/default/{type}` - Get default series for type
- `POST /api/naming-series` - Create new naming series
- `PUT /api/naming-series/{id}` - Update naming series
- `DELETE /api/naming-series/{id}` - Deactivate naming series
- `POST /api/naming-series/generate/{name}` - Generate ID from series
- `POST /api/naming-series/generate-by-type/{type}` - Generate ID by type
- `GET /api/naming-series/search?q={term}` - Search naming series
- `POST /api/naming-series/{id}/reset` - Reset series counter

### **Email Notifications**
- `POST /api/email/simulation/parent-notifications` - Test parent emails
- `POST /api/email/simulation/teacher-notifications` - Test teacher emails
- `POST /api/email/simulation/student-notifications` - Test student emails

### **Dining Access**
- `POST /api/dining/authenticate` - Biometric authentication
- `GET /api/dining/access/{registrationNumber}` - Check access status

## 🧪 **Testing**

### **Unit Tests**
```bash
mvn test
```

### **Integration Tests**
```bash
mvn verify
```

### **Email Testing**
```bash
# Test email functionality
curl -X POST "http://localhost:8081/api/email/simulation/parent-notifications"
```

## 📊 **Monitoring & Health Checks**

- **Health Check**: `GET /actuator/health`
- **Metrics**: `GET /actuator/metrics`
- **Info**: `GET /actuator/info`

## 🔍 **Code Quality with SonarQube**

- **Static Code Analysis**: Automated code quality checks
- **Code Coverage**: JaCoCo integration for test coverage
- **Code Duplication**: Detection and prevention of duplicate code
- **Security Vulnerabilities**: Automated security issue detection
- **Code Smells**: Identification of maintainability issues
- **Technical Debt**: Measurement and tracking of code quality debt
- **Quality Gates**: Automated quality checks for CI/CD pipelines

### **SonarQube Setup**
```bash
# Start SonarQube server
./setup-sonar.sh

# Run quality analysis
./run-sonar-analysis.sh

# View results at http://localhost:9000
```

### **Quality Metrics**
- **Code Coverage**: Target >80%
- **Code Duplication**: Target <3%
- **Maintainability Rating**: Target A
- **Reliability Rating**: Target A
- **Security Rating**: Target A
- **Technical Debt**: Target <1 hour

### **SonarQube Files**
- `docker-compose.sonar.yml` - SonarQube Docker configuration
- `sonar-project.properties` - SonarQube project configuration
- `setup-sonar.sh` - Start SonarQube server
- `run-sonar-simple.sh` - Run code quality analysis
- `sonar-quick-start.sh` - Complete setup guide
- `SONARQUBE_SETUP_GUIDE.md` - Detailed setup instructions

## 🔒 **Security Features**

- JWT token authentication
- Role-based access control
- Password encryption (BCrypt)
- CORS configuration
- SQL injection prevention
- XSS protection

## 📧 **Email Templates**

### **Available Templates**
- `welcome-email.html` - User welcome
- `class-reminder.html` - Class reminders
- `exam-reminder.html` - Exam notifications
- `fee-reminder.html` - Fee payment reminders
- `attendance-notification.html` - Attendance alerts
- `grade-notification.html` - Grade reports

## 🌍 **Kenya-Specific Features**

### **NEMIS Integration**
- Student registration numbers
- Government reporting
- Data synchronization

### **CBC Support**
- Competency-based curriculum
- Learning areas management
- Assessment tracking

### **TSC Compliance**
- Teacher registration
- Qualification tracking
- Performance reviews

## 📈 **Performance & Scalability**

- Connection pooling (HikariCP)
- Lazy loading for entities
- Pagination support
- Caching strategies
- Database indexing

## 🐛 **Troubleshooting**

### **Common Issues**
1. **Port 8081 already in use**: Kill existing process or change port
2. **Database connection failed**: Check PostgreSQL service and credentials
3. **Email authentication failed**: Verify Gmail app password
4. **JPA mapping errors**: Check entity relationships

### **Logs**
- Application logs: `logs/application.log`
- Email debug: `logs/email_debug.log`

## 📝 **Development Notes**

### **Code Quality**
- SonarQube integration
- No hardcoded secrets
- Proper exception handling
- Clean architecture
- Comprehensive documentation

### **Best Practices**
- DTOs for API responses
- Service layer separation
- Repository pattern
- Dependency injection
- Configuration externalization

## 🎯 **Future Enhancements**

- Mobile app integration
- Real-time notifications (WebSocket)
- Advanced reporting dashboard
- Multi-language support
- API rate limiting
- Advanced caching

## 📞 **Support**

For technical support or questions:
- Check application logs
- Review API documentation
- Test email functionality
- Verify database connectivity

---

**Built with ❤️ for Kenyan Education System**

*Last Updated: October 23, 2025*