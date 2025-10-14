# LibroNova Documentation

This directory contains the technical documentation and UML diagrams for the LibroNova Library Management System.

## UML Diagrams

### Available Diagrams

1. **Class Diagram** (`class_diagram.puml`) - Shows the complete system architecture including all classes, interfaces, relationships, and their methods and attributes.
2. **Use Case Diagram** (`use_case_diagram.puml`) - Illustrates all the use cases and actors in the system, showing what different types of users can do.

### Generating PDF Files

These diagrams are created using PlantUML syntax. To convert them to PDF format, you have several options:

#### Option 1: Using PlantUML JAR (Recommended)

1. **Download PlantUML JAR:**
   ```bash
   wget http://sourceforge.net/projects/plantuml/files/plantuml.jar/download -O plantuml.jar
   ```

2. **Generate PDF files:**
   ```bash
   # Generate Class Diagram PDF
   java -jar plantuml.jar -tpdf docs/class_diagram.puml
   
   # Generate Use Case Diagram PDF
   java -jar plantuml.jar -tpdf docs/use_case_diagram.puml
   ```

#### Option 2: Using PlantUML Online Server

1. Visit: https://www.plantuml.com/plantuml/uml/
2. Copy the content from the `.puml` files
3. Paste into the online editor
4. Generate and download as PDF

#### Option 3: Using Visual Studio Code Extension

1. Install the "PlantUML" extension in VS Code
2. Open the `.puml` files
3. Use `Ctrl+Shift+P` and search for "PlantUML: Export Current Diagram"
4. Select PDF format

#### Option 4: Using IntelliJ IDEA Plugin

1. Install the "PlantUML Integration" plugin
2. Right-click on the `.puml` file
3. Select "Show PlantUML Diagram"
4. Export as PDF from the diagram viewer

## Diagram Descriptions

### Class Diagram

The class diagram shows the complete architecture of the LibroNova system organized into the following packages:

- **Domain**: Core business entities (Book, Member, Loan) with their attributes and methods
- **Service**: Business logic layer with interfaces and implementations
- **Repository**: Data access layer with JDBC implementations  
- **UI**: User interface classes for JavaFX views
- **Exceptions**: Custom exception hierarchy for error handling
- **Infrastructure**: Utility classes for database connections and CSV export

Key architectural patterns demonstrated:
- Repository Pattern for data access
- Service Layer Pattern for business logic
- MVC Pattern for user interface
- Dependency Injection for loose coupling

### Use Case Diagram

The use case diagram illustrates the functional requirements of the system organized by feature areas:

- **Book Management**: CRUD operations for books and stock management
- **Member Management**: Member registration, activation, and management
- **Loan Management**: Creating loans, returning books, and fine calculations
- **Reports & Analytics**: Various reporting capabilities
- **System Administration**: Administrative functions
- **Self-Service**: Features available to library members

Three main actors are defined:
- **Librarian**: Primary system user with most operational capabilities
- **Library Member**: Limited access for viewing personal information
- **Library Administrator**: Full system access including administrative functions

## Additional Documentation

For more detailed information about the system, see:
- `../README.md` - Complete project documentation with setup instructions
- `../src/test/` - Unit tests that demonstrate system behavior
- `../src/main/resources/sql/schema.sql` - Database schema definition

## Notes

- All diagrams are created in English as requested
- The diagrams reflect the current system implementation
- Fine calculation is set to $1 per day for overdue books
- The system supports member number validation (numeric only)
- Complete validation and error handling is implemented throughout