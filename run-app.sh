#!/bin/bash

# Function to detect OS
detect_os() {
    case "$(uname -s)" in
        Linux*)     echo "Linux";;
        Darwin*)    echo "Mac";;
        MINGW*|CYGWIN*|MSYS*)    echo "Windows";;
        *)          echo "Unknown";;
    esac
}

# Function to check Docker
check_docker() {
    if ! docker info > /dev/null 2>&1; then
        echo "Error: Docker is not running or not installed"
        exit 1
    fi
}

# Build Docker image
build_image() {
    echo "Building Docker image..."
    docker build -t apartment-building .
}

# Function to handle errors
handle_error() {
    echo "Error: $1"
    echo "Press any key to exit..."
    read -n 1
    exit 1
}

# Main execution
OS=$(detect_os)
check_docker
build_image

echo "Starting Apartment Building Application..."

case $OS in
    "Linux")
        echo "Running on Linux..."
        xhost +local:docker
        docker run -it --rm \
            -e DISPLAY=$DISPLAY \
            -v /tmp/.X11-unix:/tmp/.X11-unix \
            -v $HOME/.Xauthority:/root/.Xauthority \
            --network host \
            apartment-building
        ;;
        
    "Mac")
        echo "Running on MacOS..."
        if ! command -v xquartz &> /dev/null; then
            handle_error "XQuartz is not installed. Please install it using: brew install --cask xquartz"
        fi
        
        # Start XQuartz if not running
        if ! pgrep -x "Xquartz" > /dev/null; then
            echo "Starting XQuartz..."
            open -a XQuartz
            sleep 2
        fi
        
        # Allow connections from Docker
        xhost + localhost
        
        # Get IP address
        IP=$(ifconfig en0 | grep inet | awk '$1=="inet" {print $2}')
        
        docker run -it --rm \
            -e DISPLAY=$IP:0 \
            -v /tmp/.X11-unix:/tmp/.X11-unix \
            apartment-building
        ;;
        
    "Windows")
        echo "Running on Windows..."
        echo "Please ensure VcXsrv is running with these settings:"
        echo "1. Multiple windows"
        echo "2. Display number: 0"
        echo "3. Start no client"
        echo "4. Disable access control"
        echo ""
        read -p "Press Enter when VcXsrv is running..."
        
        # Get Windows IP
        IP=$(ipconfig | grep "IPv4" | head -n 1 | awk '{print $NF}')
        if [ -z "$IP" ]; then
            handle_error "Could not detect IP address"
        fi
        
        echo "Using display at $IP:0.0"
        echo "Starting application..."
        
        # Run with winpty for Windows Git Bash
        if command -v winpty &> /dev/null; then
            echo "Using winpty for terminal handling..."
            if ! winpty docker run -it --rm \
                -e DISPLAY=$IP:0.0 \
                apartment-building; then
                handle_error "Failed to start application. Please check if VcXsrv is running correctly."
            fi
        else
            echo "Warning: winpty not found, trying without it..."
            if ! docker run -it --rm \
                -e DISPLAY=$IP:0.0 \
                apartment-building; then
                handle_error "Failed to start application. Please check if VcXsrv is running correctly."
            fi
        fi
        
        # Keep terminal open on error
        echo "Application closed. Press any key to exit..."
        read -n 1
        ;;
        
    *)
        handle_error "Unsupported operating system"
        ;;
esac 