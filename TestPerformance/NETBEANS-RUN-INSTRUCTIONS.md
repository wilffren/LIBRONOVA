# 🚀 How to Run LibroNova from NetBeans

## Problem
When running the project from NetBeans with "Run Project" (F6), you get the error:
```
Error: faltan los componentes de JavaFX runtime y son necesarios para ejecutar esta aplicación
```

## ✅ Solution 1: Use JavaFX Plugin (RECOMMENDED)

Instead of using "Run Project", use the JavaFX plugin:

### Method A: Terminal Command
1. **Open terminal in NetBeans** (Window → IDE Tools → Terminal)
2. **Run the command**:
   ```bash
   mvn javafx:run
   ```

### Method B: Custom NetBeans Action
1. **Right-click on the project** in NetBeans
2. **Select "Custom" → "Goals..."**
3. **Enter the goal**: `javafx:run`
4. **Click "Run"**

## ✅ Solution 2: Configure Run Project

If you want to fix the "Run Project" button:

### Step 1: Project Properties
1. **Right-click project** → **Properties**
2. **Go to "Actions"**
3. **Select "Run project"**
4. **Set Execute Goals to**: `process-classes javafx:run`

### Step 2: Alternative VM Arguments
Or you can set VM arguments:
```
--module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base
```

## ✅ Solution 3: Use Distribution Executable

If NetBeans continues to have issues:

1. **Build the project**: `mvn clean package -DskipTests`
2. **Run the executable**: `./distribution/launchers/run-libronova.sh`

## 🔧 Why This Happens

The issue occurs because:
- NetBeans uses the `exec-maven-plugin` to run Java applications
- JavaFX requires explicit module configuration since Java 11+
- The `javafx-maven-plugin` handles this automatically
- The regular exec plugin doesn't configure JavaFX modules

## 🎯 Quick Commands Summary

```bash
# Build project
mvn clean compile

# Run with JavaFX plugin
mvn javafx:run

# Build executable JAR
mvn clean package -DskipTests

# Run executable directly
./distribution/launchers/run-libronova.sh
```

## ✨ Best Practice

For development in NetBeans:
1. Use `mvn javafx:run` for testing the application
2. Use `mvn clean package -DskipTests` to build the distributable version
3. Use the launcher scripts for end-user execution

This way you get:
- ✅ Proper JavaFX module handling
- ✅ All dependencies correctly loaded  
- ✅ Consistent behavior across environments