@echo off

REM BBS Forum System Auto Startup Script
REM This script handles database updates and project startup
REM Designed for one-click deployment after code updates

REM Change to the directory where this script is located
cd /d %~dp0

echo ======================================
echo    BBS Forum System Auto Startup
echo ======================================
echo Current directory: %CD%
echo.

REM Kill any processes using port 8080
echo Checking for processes using port 8080...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8080') do (
    echo Killing process with PID: %%a
    taskkill /F /PID %%a >nul 2>&1
    echo Process killed.
)
echo Port 8080 is now free.
echo.

REM Check if running as administrator
net session >nul 2>&1
if %errorlevel% neq 0 (
    echo WARNING: Not running as administrator
    echo Some operations may require elevated privileges
    echo.
)

echo [1/4] Checking MySQL service...
REM Check if MySQL is running
mysql -u root -p123456 -e "SELECT 1;" > nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: MySQL is not accessible
    echo Please ensure:
    echo 1. MySQL service is running
    echo 2. User: root, Password: 1234
    echo 3. MySQL bin directory is in PATH
    echo.
    echo You can start MySQL service manually and try again
    pause
    exit /b 1
) else (
    echo MySQL service: OK
)

echo.
echo [2/4] Updating database...
if exist "bbs.sql" (
    echo Found bbs.sql, updating database...
    mysql -u root -p1234 --default-character-set=utf8mb4 < bbs.sql
    if %errorlevel% equ 0 (
        echo Database update: SUCCESS
    ) else (
        echo Database update: FAILED
        echo Please check bbs.sql file for syntax errors
        pause
        exit /b 1
    )
) else (
    echo ERROR: bbs.sql not found in %CD%
    echo Please ensure you are running this script from the project directory
    pause
    exit /b 1
)

echo.
echo [3/4] Building project...
if not exist "pom.xml" (
    echo ERROR: pom.xml not found in %CD%
    echo Please ensure you are running this script from the project directory
    pause
    exit /b 1
)

echo This may take a moment, please wait...
call mvn clean compile -q
if %errorlevel% neq 0 (
    echo Build: FAILED
    echo Please check:
    echo 1. Maven is installed and in PATH
    echo 2. JDK is properly configured
    echo 3. No compilation errors in source code
    echo 4. Internet connection for downloading dependencies
    pause
    exit /b 1
) else (
    echo Build: SUCCESS
)

echo.
echo [4/4] Starting application...
echo ======================================
echo BBS Forum System is starting...
echo ======================================
echo.
echo Access the application at: http://localhost:8080
echo Default accounts:
echo - Admin: admin / 123456
echo - User: u1, u2, u3 / 123456
echo.
echo Press Ctrl+C to stop the application
echo To restart: close this window and run run.bat again
echo.
echo Note: Foreign key constraints have been removed from the database
echo Data integrity is now maintained at the application level
echo.

echo Starting Spring Boot application...
call mvn spring-boot:run -q

if %errorlevel% neq 0 (
    echo ERROR: Application failed to start
    echo Check if port 8080 is available
    echo Or check application logs for errors
    pause
    exit /b 1
)

echo Application stopped.
echo You can close this window now.
pause