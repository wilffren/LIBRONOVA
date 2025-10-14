# 📁 LibroNova Project Structure

This document describes the organization of the LibroNova Library Management System project following clean architecture and layered design principles.

## 🏗️ Project Architecture

```
LibroNova/
├── 📂 src/                           # Source Code (Layered Architecture)
│   ├── 📂 main/java/com/mycompany/libronova/
│   │   ├── 📂 domain/                # Domain Layer (Entities)
│   │   │   ├── Book.java            # Book entity
│   │   │   ├── Member.java          # Member entity  
│   │   │   ├── Loan.java            # Loan entity
│   │   │   ├── LoanStatus.java      # Loan status enum
│   │   │   ├── MemberStatus.java    # Member status enum
│   │   │   └── UserRole.java        # User role enum
│   │   │
│   │   ├── 📂 repository/            # Data Access Layer
│   │   │   ├── BookRepository.java  # Book repository interface
│   │   │   ├── MemberRepository.java # Member repository interface
│   │   │   ├── LoanRepository.java  # Loan repository interface
│   │   │   └── 📂 jdbc/             # JDBC Implementations
│   │   │       ├── BookRepositoryJDBC.java
│   │   │       ├── MemberRepositoryJDBC.java
│   │   │       └── LoanRepositoryJDBC.java
│   │   │
│   │   ├── 📂 service/               # Business Logic Layer
│   │   │   ├── BookService.java     # Book service interface
│   │   │   ├── MemberService.java   # Member service interface
│   │   │   ├── LoanService.java     # Loan service interface
│   │   │   └── 📂 impl/             # Service Implementations
│   │   │       ├── BookServiceImpl.java
│   │   │       ├── MemberServiceImpl.java
│   │   │       └── LoanServiceImpl.java
│   │   │
│   │   ├── 📂 ui/                    # Presentation Layer (JavaFX)
│   │   │   ├── MainApp.java         # Main application entry point
│   │   │   ├── BookView.java        # Book management view
│   │   │   ├── MemberView.java      # Member management view
│   │   │   └── LoanView.java        # Loan management view
│   │   │
│   │   ├── 📂 exceptions/            # Custom Exception Classes
│   │   │   ├── LibroNovaException.java         # Base exception
│   │   │   ├── DatabaseException.java          # Database errors
│   │   │   ├── ValidationException.java        # Validation errors
│   │   │   ├── EntityNotFoundException.java    # Entity not found
│   │   │   ├── DuplicateISBNException.java     # Duplicate ISBN
│   │   │   ├── InactiveMemberException.java    # Inactive member
│   │   │   ├── InsufficientStockException.java # Stock errors
│   │   │   └── InvalidMemberNumberException.java # Member validation
│   │   │
│   │   └── 📂 infra/                 # Infrastructure Layer
│   │       ├── 📂 config/           # Configuration
│   │       │   └── ConnectionDB.java # Database connection
│   │       └── 📂 util/             # Utilities
│   │           └── CSVExporter.java # CSV export functionality
│   │
│   ├── 📂 main/resources/           # Application Resources
│   │   ├── database.properties      # Database configuration
│   │   └── 📂 sql/                 # SQL Scripts
│   │       └── schema.sql          # Database schema
│   │
│   └── 📂 test/java/               # Unit Tests (JUnit 5 + Mockito)
│       └── 📂 com/mycompany/libronova/service/impl/
│           ├── BookServiceImplTest.java # Book service tests
│           └── LoanServiceImplTest.java # Loan service tests
│
├── 📂 distribution/                 # Ready-to-distribute files
│   ├── 📂 executable/              # Executable JAR
│   │   └── LibroNova-executable.jar # Self-contained JAR (12MB)
│   │
│   ├── 📂 launchers/               # Application Launchers
│   │   ├── run-libronova.sh       # Bash launcher script
│   │   ├── LibroNova-final.desktop # Linux desktop entry
│   │   └── install.sh             # Installation script
│   │
│   └── 📂 documentation/           # User Documentation
│       ├── README.md              # Project documentation
│       ├── HOW-TO-RUN.md          # How to run guide
│       └── DIAGRAMS.md            # UML diagrams guide
│
├── 📂 docs/                        # Technical Documentation
│   ├── class_diagram.puml         # PlantUML class diagram
│   ├── use_case_diagram.puml      # PlantUML use case diagram
│   ├── LibroNova_Class_Diagram.pdf    # Generated class diagram
│   ├── LibroNova_Use_Case_Diagram.pdf # Generated use case diagram
│   └── 📂 icons/                  # Application icons
│
├── 📂 development-tools/           # Development & Build Tools
│   └── 📂 target/                 # Maven build output
│       ├── classes/               # Compiled classes
│       ├── test-classes/          # Test classes
│       └── libronova-1.0.0.jar   # Regular JAR (without dependencies)
│
├── 📂 logs/                        # Application Logs
│   ├── app.log                    # Current log file
│   └── app.log.*                  # Rotated log files
│
├── 📂 temp-files/                  # Temporary Files
│   ├── plantuml.jar               # PlantUML tool
│   ├── dependency-reduced-pom.xml # Maven generated file
│   └── *.csv                      # Exported CSV files
│
├── pom.xml                         # Maven configuration
└── PROJECT-STRUCTURE.md           # This file
```

## 🏛️ Architectural Layers

### 1. **Domain Layer** (`src/main/java/.../domain/`)
- **Pure business entities** with no external dependencies
- Contains core business rules and domain logic
- Entities: Book, Member, Loan
- Enums: LoanStatus, MemberStatus, UserRole

### 2. **Repository Layer** (`src/main/java/.../repository/`)
- **Data access abstraction** with interfaces and JDBC implementations
- Follows Repository pattern for database operations
- Provides data persistence without exposing database details
- JDBC implementations handle SQL operations

### 3. **Service Layer** (`src/main/java/.../service/`)
- **Business logic implementation** with validation and rules
- Orchestrates operations between repositories
- Implements business processes (loan creation, fine calculation)
- Handles transactions and business validations

### 4. **Presentation Layer** (`src/main/java/.../ui/`)
- **JavaFX user interface** with view classes
- Handles user interactions and input validation
- Presents data to users in a friendly format
- Follows MVC pattern for UI organization

### 5. **Infrastructure Layer** (`src/main/java/.../infra/`)
- **Technical concerns** like database connections
- Configuration management and utilities
- CSV export functionality and external integrations

## 🎯 Design Patterns Used

- **Repository Pattern**: Data access abstraction
- **Service Layer Pattern**: Business logic encapsulation  
- **MVC Pattern**: User interface organization
- **Dependency Injection**: Loose coupling between layers
- **Builder Pattern**: Entity construction (Loan class)
- **Singleton Pattern**: Database connection management

## 📦 Distribution Structure

The `distribution/` folder contains everything needed to run the application:

- **executable/**: Self-contained JAR with all dependencies
- **launchers/**: Scripts for easy application startup
- **documentation/**: User guides and documentation

## 🔧 Development Structure

- **src/**: Source code organized by architectural layers
- **development-tools/**: Build tools and generated artifacts
- **docs/**: Technical diagrams and developer documentation
- **logs/**: Application runtime logs
- **temp-files/**: Temporary build and export files

## 🚀 Quick Start

1. **For Users**: Run `./distribution/launchers/run-libronova.sh`
2. **For Developers**: Run `mvn clean package -DskipTests` then use launchers
3. **For Installation**: Run `./distribution/launchers/install.sh`

This structure follows **clean architecture principles** ensuring:
- ✅ **Separation of concerns**
- ✅ **Testability** with unit tests
- ✅ **Maintainability** through layered design
- ✅ **Scalability** with clear boundaries
- ✅ **Easy distribution** with organized deliverables