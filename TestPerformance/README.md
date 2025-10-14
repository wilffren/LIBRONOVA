# LibroNova - Library Management System

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.java.net/projects/jdk/17/)
[![Maven](https://img.shields.io/badge/Maven-3.6+-blue.svg)](https://maven.apache.org/)
[![JavaFX](https://img.shields.io/badge/JavaFX-17-green.svg)](https://openjfx.io/)
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

- **Programming Language**: Java 17
- **UI Framework**: JavaFX 17
- **Database**: MySQL 8.0
- **Build Tool**: Maven 3.6+
- **Testing**: JUnit 5 + Mockito
- **Architecture**: Layered architecture with Repository pattern
- **Design Patterns**: Repository, Service Layer, MVC

## 📋 Requirements

### System Requirements
- **Java Development Kit (JDK)**: 17 or higher
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

#### Install Java 17
```bash
# On Ubuntu/Debian
sudo apt update
sudo apt install openjdk-17-jdk

# On macOS (using Homebrew)
brew install openjdk@17

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

### First Time Setup
1. **Launch the Application**: Run the application using one of the methods above
2. **Main Menu**: The main menu provides access to all modules
3. **Sample Data**: The system comes with sample data for testing

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
│   │   │       │   └── ...
│   │   │       ├── repository/       # Data access layer
│   │   │       │   ├── jdbc/
│   │   │       │   └── interfaces/
│   │   │       ├── service/          # Business logic layer
│   │   │       │   ├── impl/
│   │   │       │   └── interfaces/
│   │   │       ├── ui/               # User interface
│   │   │       │   ├── BookView.java
│   │   │       │   ├── MemberView.java
│   │   │       │   ├── LoanView.java
│   │   │       │   └── MainApp.java
│   │   │       ├── exceptions/       # Custom exceptions
│   │   │       └── infra/           # Infrastructure
│   │   │           ├── config/
│   │   │           └── util/
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

#### Database Connection Failed
```bash
# Check MySQL service status
sudo systemctl status mysql

# Test connection
mysql -u libronova_user -p -e "SELECT 1;"
```

#### JavaFX Runtime Issues
```bash
# Add JavaFX modules explicitly
java --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml -jar app.jar
```

#### Permission Denied Errors
```bash
# Fix MySQL socket permissions
sudo chmod 777 /var/run/mysqld/mysqld.sock
```

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

- **v1.0.0** (2024-10-14): Initial release
  - Complete book, member, and loan management
  - JavaFX user interface
  - MySQL database integration
  - Fine calculation system
  - CSV export functionality

---

## 📸 Screenshots

### Main Menu
The central hub for accessing all library management functions.

### Book Management
Comprehensive book catalog with search and stock management capabilities.

### Loan Processing
Streamlined loan creation and return processing with fine calculation.

---

**Made with ❤️ for library management**