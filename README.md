# LibroNova - Library Management System

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/projects/jdk/21/)
[![Maven](https://img.shields.io/badge/Maven-3.6+-blue.svg)](https://maven.apache.org/)
[![JavaFX](https://img.shields.io/badge/JavaFX-21-green.svg)](https://openjfx.io/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

LibroNova is a modern, feature-rich library management system built with Java, JavaFX, and MySQL. It provides a comprehensive solution for managing books, members, loans, and library operations with an intuitive graphical user interface.

## 🚀 Features

### 📚 Book Management
- **Complete Book Catalog**: Add, update, delete, and search books
- **Stock Management**: Track available and total stock for each book
- **ISBN Validation**: Prevent duplicate ISBN entries
- **Advanced Search**: Search books by title, author, or ISBN
- **Book Details**: Store comprehensive book information (title, author, publisher, year, stock)

### 🔐 User Management & Authentication
- **Secure Login System**: Role-based authentication with username/password validation
- **User Roles**: ADMIN and ASSISTANT roles with different access levels
- **Session Management**: Secure user sessions with login/logout functionality
- **HTTP Request Simulation**: Console logging that simulates REST API calls (GET/POST/PATCH/DELETE)
- **Decorator Pattern**: Automatic assignment of default properties (role: ASSISTANT, status: ACTIVE, createdAt: now())
- **User Status Management**: Active/Inactive/Suspended user status tracking
- **Role-Based Access Control**: Admin-only features for user management
- **Enhanced Navigation**: Robust window management with independent view stages
- **Auto-Recovery**: Automatic view reinitialization on failure
- **Stability Improvements**: Comprehensive error handling and logging

### 👥 Member Management
- **Member Registration**: Register new library members with validation
- **Member Status Management**: Active/Inactive member status tracking
- **Member Search**: Find members by name or member number
- **Member Profile**: Maintain detailed member information

### 📖 Loan Management
- **Book Lending**: Create new loans with customizable loan periods
- **Return Processing**: Process book returns with overdue detection
- **Fine Calculation**: Automatic fine calculation for overdue books ($1/day)
- **Loan History**: Complete tracking of all loan transactions
- **Status Tracking**: Monitor active, returned, and overdue loans

### 📊 Reports & Analytics
- **Overdue Reports**: Generate lists of overdue loans
- **CSV Export**: Export overdue loans to CSV format
- **Member Loan History**: View all loans by specific members
- **Stock Reports**: Monitor book availability and stock levels

### 🛡️ Data Integrity & Validation
- **Input Validation**: Comprehensive validation for all data entry
- **Business Rules**: Enforce library policies (stock availability, member status)
- **Exception Handling**: Robust error handling with user-friendly messages
- **Transaction Support**: Database transaction management

## 🛠️ Technology Stack

- **Programming Language**: Java 21
- **UI Framework**: JavaFX 21
- **Database**: MySQL 8.0
- **Build Tool**: Maven 3.6+
- **Testing**: JUnit 5 + Mockito
- **Architecture**: Layered architecture with Repository pattern
- **Design Patterns**: Repository, Service Layer, MVC, Decorator Pattern
- **Security**: Role-based authentication and session management
- **Logging**: HTTP request simulation with console logging

## 📋 Requirements

### System Requirements
- **Java Development Kit (JDK)**: 21 or higher
- **Maven**: 3.6.0 or higher
- **MySQL Server**: 8.0 or higher
- **Operating System**: Windows 10+, macOS 10.15+, or Linux (Ubuntu 20.04+)
- **Memory**: Minimum 4GB RAM (8GB recommended)
- **Disk Space**: At least 500MB free space

### Development Environment
- **IDE**: IntelliJ IDEA, Eclipse, or NetBeans
- **Git**: For version control
- **MySQL Workbench**: For database management (optional but recommended)

## 🚀 Installation & Setup

### 1. Prerequisites Setup

#### Install Java 21
```bash
# On Ubuntu/Debian
sudo apt update
sudo apt install openjdk-21-jdk

# On macOS (using Homebrew)
brew install openjdk@21

# Verify installation
java -version
javac -version
```

#### Install Maven
```bash
# On Ubuntu/Debian
sudo apt install maven

# On macOS (using Homebrew)
brew install maven

# Verify installation
mvn -version
```

#### Install MySQL Server
```bash
# On Ubuntu/Debian
sudo apt update
sudo apt install mysql-server

# Start MySQL service
sudo systemctl start mysql
sudo systemctl enable mysql

# Secure installation
sudo mysql_secure_installation
```

### 2. Database Setup

#### Create Database and User
```sql
-- Connect to MySQL as root
mysql -u root -p

-- Create database
CREATE DATABASE libronova_db;

-- Create user (replace 'your_password' with a secure password)
CREATE USER 'libronova_user'@'localhost' IDENTIFIED BY 'your_password';

-- Grant privileges
GRANT ALL PRIVILEGES ON libronova_db.* TO 'libronova_user'@'localhost';
FLUSH PRIVILEGES;

-- Exit MySQL
EXIT;
```

#### Initialize Database Schema
```bash
# Navigate to project directory
cd /path/to/LibroNova

# Run the SQL schema file
mysql -u libronova_user -p libronova_db < src/main/resources/sql/schema.sql
```

### 3. Project Setup

#### Clone the Repository
```bash
git clone https://github.com/your-username/libronova.git
cd libronova
```

#### Configure Database Connection
Edit `src/main/resources/database.properties`:
```properties
# Database Configuration
db.url=jdbc:mysql://localhost:3306/libronova_db
db.username=libronova_user
db.password=your_password
db.driver=com.mysql.cj.jdbc.Driver

# Connection Pool Settings
db.pool.initial=5
db.pool.max=20
db.pool.timeout=30000
```

#### Build the Project
```bash
# Clean and compile the project
mvn clean compile

# Run tests (optional)
mvn test

# Create executable JAR with dependencies
mvn clean package
```

## 🏃‍♂️ Running the Application

### 🚀 Quick Start
```bash
# Run the application
mvn javafx:run

# Login with default credentials:
# Admin: admin / admin123
# Assistant: assistant / assistant123
```

### Method 1: Using Maven
```bash
# Run directly with Maven
mvn javafx:run

# Or use exec plugin
mvn exec:java -Dexec.mainClass="com.mycompany.libronova.ui.MainApp"
```

### Method 2: Using JAR file
```bash
# After building with mvn package
java -jar target/libronova-1.0.0-jar-with-dependencies.jar
```

### Method 3: Development Mode
```bash
# For development with auto-reload
mvn javafx:run -Djavafx.args="--enable-preview"
```

## 🎯 Usage Guide

### 🔑 Default Login Credentials

The application includes default user accounts for testing and initial setup:

#### Administrator Account
- **Username**: `admin`
- **Password**: `admin123`
- **Role**: ADMIN
- **Access**: Full system access including user management

#### Assistant Account
- **Username**: `assistant` 
- **Password**: `assistant123`
- **Role**: ASSISTANT
- **Access**: Standard library operations (books, members, loans)

> 📝 **Note**: These credentials are automatically created when the application starts. In a production environment, make sure to change these default passwords.

### First Time Setup
1. **Launch the Application**: Run the application using one of the methods above
2. **Login Screen**: Use one of the default credentials above to log in
3. **Main Menu**: After successful login, the main menu provides access to all modules based on your role
4. **Sample Data**: The system comes with sample data for testing

### ⚡ Quick Troubleshooting
**If the application doesn't start or behaves unexpectedly**:
```bash
# 1. Clean build and restart
mvn clean compile
mvn javafx:run

# 2. Check Java version (must be 21+)
java --version

# 3. Verify database connection
mysql -u libronova_user -p -e "SELECT 1;"
```

**If login works but menu doesn't appear**:
- Check console output for error messages
- Look for HTTP simulation logs indicating successful authentication
- Ensure JavaFX runtime is properly configured

### Book Management
1. **Add New Book**: Click "Book Management" → "Add New Book"
2. **Required Fields**: Fill in ISBN, Title, Author, Publisher, Year, and Stock
3. **Search Books**: Use the search functionality to find specific books
4. **Update Stock**: Modify stock levels as needed

### Member Management
1. **Register Member**: Click "Member Management" → "Add New Member"
2. **Member Information**: Provide name, email, and contact details
3. **Member Status**: Set member as Active or Inactive

### Loan Processing
1. **Create Loan**: Select book and member, specify loan duration
2. **Return Book**: Select active loan and process return
3. **View Loans**: Monitor all active, returned, and overdue loans
4. **Calculate Fines**: System automatically calculates fines for overdue books

### User Management (Admin Only)
1. **Access Control**: Only users with ADMIN role can access user management features
2. **Create User with Defaults**: Demonstrates decorator pattern - creates users with default properties:
   - Default Role: ASSISTANT
   - Default Status: ACTIVE
   - Default CreatedAt: Current timestamp
3. **List All Users**: View all system users with their roles and status
4. **Update User Status**: Change user status (Active/Inactive/Suspended)
5. **HTTP Logging**: All user operations are logged to console simulating REST API calls
6. **Session Management**: Secure login/logout with session tracking

### 🚀 **Navigation & Window Management**
The application uses an enhanced window management system:
- **Independent Windows**: Each view (Books, Members, Loans) opens in its own window
- **Non-blocking Navigation**: Main menu remains available while views are open
- **Seamless Return**: "Back to Main Menu" buttons return you to the main interface
- **Multi-window Support**: You can have multiple views open simultaneously
- **Auto-recovery**: If a view fails to load, the system will attempt to reinitialize it

## 🧪 Testing

### Run All Tests
```bash
mvn test
```

### Run Specific Test Classes
```bash
# Run Book Service tests
mvn test -Dtest=BookServiceImplTest

# Run Loan Service tests
mvn test -Dtest=LoanServiceImplTest
```

### Test Coverage
The project includes comprehensive unit tests covering:
- **Book Service**: Stock validation, ISBN uniqueness, CRUD operations
- **Loan Service**: Fine calculation, loan creation, return processing
- **Repository Layer**: Database operations and data integrity
- **Business Logic**: Validation rules and business constraints

### Test Reports
After running tests, reports are available in:
- `target/surefire-reports/`: JUnit test reports
- `target/site/jacoco/`: Code coverage reports (if configured)

## 📁 Project Structure

```
LibroNova/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/mycompany/libronova/
│   │   │       ├── domain/           # Entity classes
│   │   │       │   ├── Book.java
│   │   │       │   ├── Member.java
│   │   │       │   ├── Loan.java
│   │   │       │   ├── SystemUser.java      # Authentication user entity
│   │   │       │   ├── UserRole.java        # User roles enum
│   │   │       │   ├── UserStatus.java      # User status enum
│   │   │       │   └── ...
│   │   │       ├── repository/       # Data access layer
│   │   │       │   ├── jdbc/
│   │   │       │   └── interfaces/
│   │   │       ├── service/          # Business logic layer
│   │   │       │   ├── impl/
│   │   │       │   │   ├── AuthenticationServiceImpl.java
│   │   │       │   │   └── ...
│   │   │       │   ├── decorator/           # Decorator pattern
│   │   │       │   │   └── UserCreationDecorator.java
│   │   │       │   ├── AuthenticationService.java
│   │   │       │   └── interfaces/
│   │   │       ├── ui/               # User interface
│   │   │       │   ├── BookView.java
│   │   │       │   ├── MemberView.java
│   │   │       │   ├── LoanView.java
│   │   │       │   ├── LoginView.java       # Login screen
│   │   │       │   └── MainApp.java         # Updated with authentication
│   │   │       ├── exceptions/       # Custom exceptions
│   │   │       └── infra/           # Infrastructure
│   │   │           ├── config/
│   │   │           └── util/
│   │   │               └── HTTPLogger.java   # HTTP simulation logging
│   │   └── resources/
│   │       ├── sql/                  # Database schema
│   │       ├── database.properties   # Configuration
│   │       └── styles/              # CSS stylesheets
│   └── test/
│       └── java/                    # Unit tests
├── target/                          # Build output
├── pom.xml                         # Maven configuration
└── README.md                       # This file
```

## 🔧 Configuration

### Database Configuration
Modify `src/main/resources/database.properties` for different environments:

```properties
# Development
db.url=jdbc:mysql://localhost:3306/libronova_dev
db.username=dev_user
db.password=dev_password

# Production
db.url=jdbc:mysql://production-server:3306/libronova_prod
db.username=prod_user
db.password=secure_password
```

### Application Settings
Configure application behavior in `application.properties`:

```properties
# Fine calculation
loan.daily_fine_rate=1.0
loan.max_days=365

# UI Settings
ui.theme=default
ui.language=en
```

## 🐛 Troubleshooting

### Common Issues

#### 🚪 Login Screen Not Appearing
```bash
# If the login screen doesn't show, check console for errors
# Ensure JavaFX modules are properly loaded:
mvn clean compile
mvn javafx:run

# Check if application starts but no window appears:
# This usually indicates a JavaFX initialization issue
```

#### 💻 Application Closes After Login
**Solution**: This issue has been fixed in v1.1.1. If you still experience this:
1. Ensure you're using the latest version
2. Check console logs for error messages
3. Verify that services are properly initialized

#### 📁 Views Don't Open or Close Immediately
**Symptoms**: Clicking on "Book Management", "Member Management", etc. does nothing
**Solutions**:
```bash
# 1. Check console for detailed error messages
# Look for lines starting with [SEVERE] or [ERROR]

# 2. Verify database connection
mysql -u libronova_user -p -e "SELECT 1;"

# 3. Restart application with clean compile
mvn clean compile
mvn javafx:run
```

#### 🔑 Authentication Issues
**Default Credentials Not Working**:
- Admin: `admin` / `admin123`
- Assistant: `assistant` / `assistant123`
- Credentials are case-sensitive
- Check console for HTTP simulation logs to verify authentication attempts

#### Database Connection Failed
```bash
# Check MySQL service status
sudo systemctl status mysql

# Test connection
mysql -u libronova_user -p -e "SELECT 1;"

# Restart MySQL if needed
sudo systemctl restart mysql
```

#### JavaFX Runtime Issues
```bash
# If you see "JavaFX runtime components are missing"
# The project is configured for Java 21 with JavaFX 21
# Ensure you have the correct versions:

java --version  # Should show Java 21
mvn -version   # Should show Maven 3.6+

# Run with explicit module path if needed:
mvn clean javafx:run
```

#### 🖥️ Window Management Issues
**Multiple windows not working properly**:
1. Close all application windows
2. Restart the application
3. Each view should open in its own independent window
4. Use "Back to Main Menu" to return to the main interface

#### Permission Denied Errors
```bash
# Fix MySQL socket permissions
sudo chmod 777 /var/run/mysqld/mysqld.sock

# Or restart MySQL service
sudo systemctl restart mysql
```

### 📊 Application Logs and Debugging

#### Console Output
The application provides detailed logging:
```bash
# HTTP simulation logs show all user operations:
========================================
HTTP REQUEST SIMULATION
========================================
Method: POST
Endpoint: /api/auth/login
Status: 201 Created
========================================

# Authentication logs:
[AUTH] 2024-10-14 17:22:59 - Authentication attempt for user 'admin': SUCCESS

# Session management:
[SESSION] 2024-10-14 17:22:59 - User 'admin': LOGIN
```

#### Error Levels
- **[INFO]**: Normal operation messages
- **[WARNING]**: Non-critical issues that don't stop execution
- **[SEVERE]**: Critical errors that may cause application failure
- **[ERROR]**: General errors with stack traces

### 📝 Known Issues and Limitations

#### Current Limitations
- **In-Memory User Storage**: User authentication uses in-memory storage for demonstration. In production, integrate with a proper user database.
- **Plain Text Passwords**: Passwords are stored in plain text for demo purposes. Production systems should use proper hashing (bcrypt, etc.).
- **HTTP Simulation**: The HTTP logging system simulates REST API calls for demonstration. Real implementations would use actual HTTP clients.

#### Future Enhancements
- Database-backed user authentication
- Password encryption and security improvements
- Real REST API integration
- Advanced user management features
- Audit logging for all user actions

### Log Files
Check application logs in:
- `logs/application.log`: General application logs
- `logs/error.log`: Error logs
- Console output during development

## 🤝 Contributing

1. **Fork the Repository**: Create a personal copy
2. **Create Feature Branch**: `git checkout -b feature/new-feature`
3. **Commit Changes**: `git commit -m "Add new feature"`
4. **Push Branch**: `git push origin feature/new-feature`
5. **Open Pull Request**: Submit your changes for review

### Development Guidelines
- Follow Java coding conventions
- Write comprehensive unit tests
- Update documentation for new features
- Use meaningful commit messages

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Author

**Wilffren Muñoz**
- GitHub: [@your-username](https://github.com/your-username)
- Email: your.email@example.com

## 🆘 Support

For support and questions:
- **Issues**: [GitHub Issues](https://github.com/your-username/libronova/issues)
- **Discussions**: [GitHub Discussions](https://github.com/your-username/libronova/discussions)
- **Email**: support@libronova.com

## 🔄 Version History

- **v1.1.1** (2024-10-14): Bug Fixes & Navigation Improvements
  - 🐛 **FIXED**: Application no longer closes unexpectedly after login
  - 🐛 **FIXED**: Main menu now displays correctly after successful authentication
  - 🔧 **IMPROVED**: Enhanced window management with independent stages for each view
  - 🔧 **IMPROVED**: Better error handling and logging throughout the application
  - 🔧 **IMPROVED**: Robust navigation system with proper back-to-menu functionality
  - ⚡ **PERFORMANCE**: Optimized view initialization and stage management
  - 🔒 **STABILITY**: Added null checks and auto-recovery for view components

- **v1.1.0** (2024-10-14): Authentication & User Management Update
  - ✨ **NEW**: User authentication system with role-based access control
  - ✨ **NEW**: Admin and Assistant user roles with different permissions
  - ✨ **NEW**: HTTP request simulation logging (GET/POST/PATCH/DELETE)
  - ✨ **NEW**: Decorator pattern for user creation with default properties
  - ✨ **NEW**: Secure login/logout with session management
  - ✨ **NEW**: User management interface (Admin only)
  - ⬆️ **UPDATED**: Upgraded to Java 21 and JavaFX 21
  - ⬆️ **UPDATED**: Enhanced main application with authentication flow
  - 🔐 **SECURITY**: Default credentials: admin/admin123, assistant/assistant123

- **v1.0.0** (2024-10-14): Initial release
  - Complete book, member, and loan management
  - JavaFX user interface
  - MySQL database integration
  - Fine calculation system
  - CSV export functionality

---

## 📸 Screenshots

### Login Screen
Secure authentication with role-based access control. Default credentials are provided for testing.

### Main Menu
The central hub for accessing all library management functions. Shows user information and role-specific options.

### Book Management
Comprehensive book catalog with search and stock management capabilities. Opens in independent window.

### User Management (Admin Only)
Demonstrates decorator pattern and HTTP logging simulation for user operations.

### Loan Processing
Streamlined loan creation and return processing with fine calculation and overdue management.

---

Name: Wilffren E. Muñoz V.
Clan: Cienaga Java.
Email: wilffren14@gmail.com
Document: CC

**Made with ❤️ for library management**
