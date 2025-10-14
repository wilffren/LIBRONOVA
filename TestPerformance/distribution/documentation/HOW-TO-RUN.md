# 🚀 How to Run LibroNova with Double Click

This document explains the different ways to run LibroNova without using the terminal.

## 📁 Generated Files

After running `mvn clean package -DskipTests`, several files have been created:

- `executable/LibroNova-executable.jar` - **Executable JAR with all dependencies**
- `launchers/run-libronova.sh` - **Executable bash script**
- `launchers/LibroNova-final.desktop` - **Desktop application file for Ubuntu**

## 🖱️ Methods to Run with Double Click

### Method 1: Bash Script (RECOMMENDED)

1. **Navigate to the project directory:**
   ```bash
   cd /home/Coder/NetBeansProjects/LIBRONOVA/TestPerformance
   ```

2. **Double click on `launchers/run-libronova.sh`** in the file manager
   - In Ubuntu, a dialog will open asking if you want to run it
   - Select "Execute" or "Run in Terminal"

3. **Or from terminal (one time only):**
   ```bash
   ./distribution/launchers/run-libronova.sh
   ```

### Method 2: Desktop File (For applications menu)

1. **Copy the desktop file to the applications directory:**
   ```bash
   cp distribution/launchers/LibroNova-final.desktop ~/.local/share/applications/
   ```

2. **Or copy it to the desktop:**
   ```bash
   cp distribution/launchers/LibroNova-final.desktop ~/Desktop/
   ```

3. **Make the file executable if necessary:**
   ```bash
   chmod +x ~/Desktop/LibroNova-final.desktop
   ```

4. **Now you can:**
   - Double click the desktop icon
   - Search for "LibroNova" in the applications menu
   - Run from the application launcher

### Method 3: Direct Executable JAR

If you have Java configured to run JAR files with double click:

1. **Navigate to the `distribution/executable/` folder**
2. **Double click on `LibroNova-executable.jar`**

## ⚙️ Automatic System Configuration

### Automatic Installation Script

Run this command to configure LibroNova automatically:

```bash
# Make the script executable
chmod +x distribution/launchers/run-libronova.sh

# Copy to applications menu
cp distribution/launchers/LibroNova-final.desktop ~/.local/share/applications/

# Update applications database
update-desktop-database ~/.local/share/applications/

echo "✅ LibroNova configured successfully!"
echo "Now you can find it in the applications menu or double click run-libronova.sh"
```

## 🛠️ Troubleshooting

### If double click doesn't work:

1. **Verify Java is installed:**
   ```bash
   java -version
   ```

2. **Verify the JAR exists:**
   ```bash
   ls -la distribution/executable/LibroNova-executable.jar
   ```

3. **Run manually to see errors:**
   ```bash
   java -jar distribution/executable/LibroNova-executable.jar
   ```

### If you get permission errors:

```bash
chmod +x distribution/launchers/run-libronova.sh
chmod +x distribution/launchers/LibroNova-final.desktop
```

### If it doesn't appear in the applications menu:

```bash
# Update menu cache
update-desktop-database ~/.local/share/applications/

# Or restart the desktop environment
gnome-shell --replace &
```

## 📋 System Requirements

- **Java 17 or higher** installed
- **Ubuntu 20.04+** (or any Linux distribution with GNOME/KDE)
- **Execution permissions** on files

## 🎯 Recommended Usage

For the **best experience**, we recommend using **Method 1 (Bash Script)** because:

- ✅ Includes error checking
- ✅ Shows informative messages
- ✅ Verifies Java version
- ✅ Works from any location
- ✅ Handles errors gracefully

## 📦 Distribution to Other Users

To share the application with other users:

1. **Compress the entire distribution folder:**
   ```bash
   tar -czf LibroNova-v1.0.0.tar.gz distribution/
   ```

2. **Or create a package with only necessary files:**
   ```bash
   mkdir LibroNova-Distributable
   cp -r distribution/ LibroNova-Distributable/
   cp README.md LibroNova-Distributable/
   cp pom.xml LibroNova-Distributable/
   ```

3. **Instructions for end user:**
   - Extract the file
   - Run `./distribution/launchers/run-libronova.sh`
   - Or double click the file

## 🔧 Customization

To change the application icon:
1. Place your icon at `docs/icons/libronova.png`
2. Regenerate the desktop file if necessary

To change the application name:
- Edit `distribution/launchers/LibroNova-final.desktop`
- Change the line `Name=LibroNova` to desired name

---

**Ready!** Now you can run LibroNova simply by double clicking the corresponding file. 🎉