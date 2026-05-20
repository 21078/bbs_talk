#!/bin/bash

# BBS Forum System Auto Startup Script
# This script handles database updates and project startup
# Designed for one-click deployment after code updates
# Compatible with Ubuntu 22.04

# Exit on any error
set -e

# Change to the directory where this script is located
cd "$(dirname "${BASH_SOURCE[0]}")"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "======================================"
echo -e "    BBS Forum System Auto Startup"
echo -e "======================================"
echo -e "Current directory: ${BLUE}$(pwd)${NC}"
echo ""

# Function to check if a command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Function to kill processes using port 8080
echo -e "${YELLOW}Checking for processes using port 8080...${NC}"
pids=$(lsof -ti:8080 2>/dev/null || ss -tulpn | grep ':8080' | awk '{print $6}' | cut -d',' -f2 | cut -d'=' -f2 || echo "")

if [ -n "$pids" ]; then
    for pid in $pids; do
        if [[ "$pid" =~ ^[0-9]+$ ]]; then
            echo -e "Killing process with PID: ${RED}$pid${NC}"
            kill -9 "$pid" 2>/dev/null || true
            echo -e "Process ${GREEN}killed${NC}."
        fi
    done
else
    echo -e "No processes found using port 8080."
fi
echo -e "Port 8080 is now ${GREEN}free${NC}."
echo ""

# Check if running as root (not recommended for security)
if [ "$EUID" -eq 0 ]; then
    echo -e "${YELLOW}WARNING: Running as root user${NC}"
    echo "This is not recommended for security reasons"
    echo ""
fi

echo -e "[1/4] ${BLUE}Checking MySQL service...${NC}"
# Check if MySQL is running
if command_exists mysql; then
    if mysql -u root -pyour_password -e "SELECT 1;" >/dev/null 2>&1; then
        echo -e "MySQL service: ${GREEN}OK${NC}"
    else
        echo -e "${RED}ERROR: MySQL is not accessible${NC}"
        echo "Please ensure:"
        echo "1. MySQL service is running"
        echo "2. User: root, Password: your_password"
        echo "3. MySQL is properly installed"
        echo ""
        echo "You can start MySQL service manually with:"
        echo "sudo systemctl start mysql"
        exit 1
    fi
else
    echo -e "${RED}ERROR: MySQL client is not installed${NC}"
    echo "Please install MySQL client:"
    echo "sudo apt update && sudo apt install mysql-client"
    exit 1
fi

echo ""
echo -e "[2/4] ${BLUE}Updating database...${NC}"
if [ -f "bbs.sql" ]; then
    echo "Found bbs.sql, updating database..."
    if mysql -u root -pyour_password --default-character-set=utf8mb4 < bbs.sql; then
        echo -e "Database update: ${GREEN}SUCCESS${NC}"
    else
        echo -e "Database update: ${RED}FAILED${NC}"
        echo "Please check bbs.sql file for syntax errors"
        exit 1
    fi
else
    echo -e "${RED}ERROR: bbs.sql not found in $(pwd)${NC}"
    echo "Please ensure you are running this script from the project directory"
    exit 1
fi

echo ""
echo -e "[3/4] ${BLUE}Building project...${NC}"
if [ ! -f "pom.xml" ]; then
    echo -e "${RED}ERROR: pom.xml not found in $(pwd)${NC}"
    echo "Please ensure you are running this script from the project directory"
    exit 1
fi

# Check if Maven is installed
if ! command_exists mvn; then
    echo -e "${RED}ERROR: Maven is not installed${NC}"
    echo "Please install Maven:"
    echo "sudo apt update && sudo apt install maven"
    exit 1
fi

# Check if Java is installed
if ! command_exists java; then
    echo -e "${RED}ERROR: Java is not installed${NC}"
    echo "Please install Java 8 or higher:"
    echo "sudo apt update && sudo apt install openjdk-8-jdk"
    exit 1
fi

echo "This may take a moment, please wait..."
if mvn clean compile -q; then
    echo -e "Build: ${GREEN}SUCCESS${NC}"
else
    echo -e "Build: ${RED}FAILED${NC}"
    echo "Please check:"
    echo "1. Maven is installed and in PATH"
    echo "2. JDK is properly configured"
    echo "3. No compilation errors in source code"
    echo "4. Internet connection for downloading dependencies"
    exit 1
fi

echo ""
echo -e "[4/4] ${BLUE}Starting application...${NC}"
echo -e "======================================"
echo -e "BBS Forum System is ${GREEN}starting...${NC}"
echo -e "======================================"
echo ""
echo -e "Access the application at: ${BLUE}http://localhost:8080${NC}"
echo "Default accounts:"
echo "- Admin: admin / your_password"
echo "- User: u1, u2, u3 / your_password"
echo ""
echo "Press Ctrl+C to stop the application"
echo "To restart: close this terminal and run ./run.sh again"
echo ""
echo -e "${YELLOW}Note: Foreign key constraints have been removed from the database${NC}"
echo -e "${YELLOW}Data integrity is now maintained at the application level${NC}"
echo ""

echo -e "${BLUE}Starting Spring Boot application...${NC}"
if mvn spring-boot:run; then
    echo -e "Application stopped."
    echo "You can close this terminal now."
else
    echo -e "${RED}ERROR: Application failed to start${NC}"
    echo "Check if port 8080 is available"
    echo "Or check application logs for errors"
    exit 1
fi