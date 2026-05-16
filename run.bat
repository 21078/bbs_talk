@echo off

REM Simple BBS startup script for Windows
REM Avoids Chinese characters to prevent encoding issues

echo BBS Forum System Startup
echo ============================
echo.
echo Step 1: Checking port 8080...
netstat -ano | findstr :8080 > nul
if %errorlevel% equ 0 (
    echo Port 8080 is in use, attempting to free it...
    for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8080') do (
        taskkill /F /PID %%a > nul 2>&1
    )
) else (
    echo Port 8080 is free
)

echo.
echo Step 2: Updating database...
mysql -u root -p1234 -e "SHOW DATABASES;" > nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: MySQL not accessible
    echo Please ensure MySQL is running with user: root, password: 1234
    pause
    exit /b 1
)

echo Looking for bbs.sql...
if exist "bbs.sql" (
    echo Found bbs.sql, executing...
    mysql -u root -p1234 < bbs.sql
    if %errorlevel% equ 0 (
        echo Database updated successfully
    ) else (
        echo Error executing SQL file
    )
) else (
    echo Warning: bbs.sql not found in current directory
    echo Current directory: %CD%
    dir *.sql
)

echo.
echo Step 3: Compiling project...
echo This may take a minute, please wait...
mvn clean compile
if %errorlevel% neq 0 (
    echo ERROR: Compilation failed
    echo Check if Maven and JDK are properly installed
    pause
    exit /b 1
)

echo.
echo Step 4: Starting application...
echo Starting BBS Forum System...
echo Access: http://localhost:8080
echo Admin: admin / 123456
echo.

mvn spring-boot:run

if %errorlevel% neq 0 (
    echo ERROR: Application failed to start
    pause
    exit /b 1
)

pause