#!/bin/bash

# LibroNova - Library Management System Launcher
# This script launches the LibroNova application

# Get the directory where this script is located
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Path to the executable JAR
JAR_FILE="$SCRIPT_DIR/../executable/LibroNova-executable.jar"

# Check if the JAR file exists
if [ ! -f "$JAR_FILE" ]; then
    echo "Error: LibroNova executable not found at $JAR_FILE"
    echo "Please run 'mvn clean package -DskipTests' first to build the application."
    read -p "Press Enter to close..."
    exit 1
fi

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "Error: Java is not installed or not in PATH"
    echo "Please install Java 17 or higher to run LibroNova"
    read -p "Press Enter to close..."
    exit 1
fi

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d '"' -f 2 | cut -d '.' -f 1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo "Warning: Java 17 or higher is recommended for LibroNova"
    echo "Current Java version: $(java -version 2>&1 | head -n 1)"
    echo "The application may not work correctly with older Java versions"
    echo ""
fi

echo "Starting LibroNova Library Management System..."
echo "JAR file: $JAR_FILE"
echo "Working directory: $SCRIPT_DIR"
echo ""

# Change to the script directory to ensure relative paths work
cd "$SCRIPT_DIR"

# Launch the application
java -jar "$JAR_FILE"

# If the application exits with an error, show a message
if [ $? -ne 0 ]; then
    echo ""
    echo "LibroNova exited with an error."
    echo "Please check the error messages above."
    read -p "Press Enter to close..."
fi