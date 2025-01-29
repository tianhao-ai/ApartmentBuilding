# Apartment Building Temperature Control System

A Java Swing application for managing temperature control in an apartment building.

## Project Structure

```
ApartmentBuilding/
├── src/main/java/
│   ├── models/
│   │   ├── Room.java          # Base class for all rooms
│   │   ├── Apartment.java     # Apartment implementation
│   │   ├── CommonRoom.java    # Common area implementation
│   │   └── Building.java      # Main building management
│   ├── gui/
│   │   └── BuildingGUI.java   # Graphical user interface
│   ├── utils/
│   │   └── PropertyLoader.java # Configuration utilities
│   └── Main.java              # Application entry point
├── src/main/resources/
│   └── application.properties  # Application configuration
├── build.gradle               # Gradle build configuration
├── Dockerfile                 # Docker configuration
├── run-app.sh                 # Platform-specific run script
├── gradlew                    # Gradle wrapper script
├── apartment-building.jar    # Built JAR file
└── README.md                  # This file
```

## Features

- Real-time temperature monitoring
- Individual room temperature control
- Support for apartments and common areas
- Automatic temperature adjustment
- User-friendly graphical interface

## Running the Application

There are three ways to run this application, listed from simplest to most complex:

### Method 1: Direct JAR Execution (Simplest)

**Prerequisites:**
- Java 17 or later installed

**Steps:**
1. Clone the repository:
```bash
git clone https://github.com/tianhao-ai/ApartmentBuilding.git
cd apartment-building
```
2. Run the application:
```bash
java -jar apartment-building.jar
```

### Method 2: Using Docker (Recommended for Consistent Environment)

**Prerequisites:**
- Docker installed and running
- X11 server except for Linux user

**Platform-Specific Instructions:**

#### Linux
1. Run:
```bash
chmod +x run-app.sh
./run-app.sh
```

#### macOS
1. Install XQuartz:
```bash
brew install --cask xquartz
```

2. Configure XQuartz properly:
   - Start XQuartz (from Applications/Utilities or using Spotlight)
   - Open XQuartz Preferences (⌘+,)
   - Go to the "Security" tab
   - Uncheck "Authenticate connections"
   - Check "Allow connections from network clients"
   - Close Preferences
   - Restart XQuartz for changes to take effect

3. If you encounter "cannot connect to X11 window server" error:
   - Make sure you've completed step 2 above
   - Quit XQuartz completely (⌘+Q)
   - Wait a few seconds
   - Start XQuartz again
   - Try running the application again

4. Run:
```bash
chmod +x run-app.sh
./run-app.sh
```

#### Windows
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

4. Run:
```bash
chmod +x run-app.sh
./run-app.sh
```

#### Docker Method Troubleshooting

See the troubleshooting sections under each platform's instructions above.

### Method 3: Building from Source (For Developers)

**Prerequisites:**
- JDK 17 or later installed
- Git installed

**Steps:**
1. Clone the repository:
```bash
git clone https://github.com/tianhao-ai/ApartmentBuilding.git
cd apartment-building
```

2. Build the project:
```bash
./gradlew clean build
```

3. Run the built application:
```bash
java -jar build/libs/apartment-building.jar
```

## Troubleshooting Common Issues


### Java Version Issues
```bash
# Check Java version
java -version
```
Make sure you have Java 17 or later installed.

### Permission Issues
If you encounter permission denied errors:
```bash
# Make scripts executable
chmod +x gradlew
chmod +x run-app.sh
```
