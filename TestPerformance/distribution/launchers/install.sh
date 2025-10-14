#!/bin/bash

# LibroNova Installation Script
# This script installs LibroNova for easy access from the applications menu

echo "🚀 LibroNova Installation Script"
echo "================================"

# Get the directory where this script is located
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(cd "$SCRIPT_DIR/../.." && pwd)"

echo "Project directory: $PROJECT_DIR"

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "❌ Error: Java is not installed"
    echo "Please install Java 17 or higher to run LibroNova"
    exit 1
fi

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d '"' -f 2 | cut -d '.' -f 1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo "⚠️  Warning: Java 17 or higher is recommended"
    echo "Current version: $(java -version 2>&1 | head -n 1)"
fi

# Check if executable exists
if [ ! -f "$PROJECT_DIR/distribution/executable/LibroNova-executable.jar" ]; then
    echo "❌ Error: LibroNova executable not found"
    echo "Please run 'mvn clean package -DskipTests' first"
    exit 1
fi

# Make scripts executable
chmod +x "$SCRIPT_DIR/run-libronova.sh"
chmod +x "$SCRIPT_DIR/LibroNova-final.desktop"

echo "✅ Made launcher scripts executable"

# Copy desktop file to applications directory
mkdir -p ~/.local/share/applications/
cp "$SCRIPT_DIR/LibroNova-final.desktop" ~/.local/share/applications/

echo "✅ Installed desktop entry"

# Update applications database
if command -v update-desktop-database &> /dev/null; then
    update-desktop-database ~/.local/share/applications/
    echo "✅ Updated applications database"
fi

echo ""
echo "🎉 LibroNova installed successfully!"
echo ""
echo "You can now:"
echo "• Search for 'LibroNova' in your applications menu"
echo "• Double-click on 'run-libronova.sh' in the launchers folder"
echo "• Run from command line: ./distribution/launchers/run-libronova.sh"
echo ""
echo "Enjoy using LibroNova! 📚"