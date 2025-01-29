# Apartment Building Temperature Control System

A Java Swing application for managing temperature control in an apartment building.

## Prerequisites

- Docker installed and running
- X11 server (varies by operating system):
  - Linux: Built-in (no action needed)
  - macOS: XQuartz
  - Windows: VcXsrv

## Setup Instructions by Platform

### Linux
1. No additional setup required
2. Run:
```bash
chmod +x run-app.sh
./run-app.sh
```

### macOS
1. Install XQuartz:
```bash
brew install --cask xquartz
```
2. Start XQuartz and enable "Allow connections from network clients" in Preferences
3. Run:
```bash
chmod +x run-app.sh
./run-app.sh
```

### Windows
1. Install VcXsrv Windows X Server:
   - Download from: https://sourceforge.net/projects/vcxsrv/
   - Install with default settings

2. Start XLaunch (VcXsrv) with these settings:
   - Multiple windows
   - Display number: 0
   - Start no client
   - Disable access control (check the box)

3. Install Git Bash if not already installed
   - Download from: https://git-scm.com/download/win
   - During installation, select "Use MinTTY" option

4. Run the application:
   - Open Git Bash
   - Navigate to project directory
   - Run:
```bash
chmod +x run-app.sh
./run-app.sh
```

### Windows Troubleshooting

1. If you see "input device is not a TTY" error:
   - Make sure you're using Git Bash (MinTTY)
   - The script will automatically use winpty if available
   - If issues persist, try running Git Bash as Administrator

2. If the application window doesn't appear:
   - Make sure VcXsrv is running and configured correctly
   - Check Windows Defender Firewall settings:
     - Open Windows Defender Firewall
     - Allow VcXsrv through both private and public networks
   - Try restarting VcXsrv and running the script again

3. If you get a display error:
   - Make sure no other X server is running
   - Check that the DISPLAY variable is set correctly
   - Try running XLaunch again with the settings mentioned above

4. Common VcXsrv Settings Issues:
   - Always use "Multiple Windows" mode
   - Make sure "Disable access control" is checked
   - Native opengl should be disabled
   - No client launch needed

## Features

- Real-time temperature monitoring
- Individual room temperature control
- Support for apartments and common areas
- Automatic temperature adjustment
- User-friendly graphical interface

## Building from Source

If you want to build without Docker:

1. Ensure JDK 17 is installed
2. Build:
```bash
./gradlew build
```
3. Run:
```bash
java -jar build/libs/apartment-building.jar
```