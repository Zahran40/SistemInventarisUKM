@echo off
REM Script untuk compile dan run aplikasi tanpa Maven

echo ========================================
echo Sistem Inventaris UKM - Build Script
echo ========================================
echo.

REM Check if lib folder exists
if not exist "lib" (
    echo Creating lib folder...
    mkdir lib
)

REM Check if MySQL Connector exists
if not exist "lib\mysql-connector-j-8.0.33.jar" (
    echo Downloading MySQL JDBC Driver...
    powershell -Command "Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.0.33/mysql-connector-j-8.0.33.jar' -OutFile 'lib\mysql-connector-j-8.0.33.jar'"
    echo Done!
    echo.
)

REM Create target/classes folder
if not exist "target\classes" (
    mkdir target\classes
)

echo Compiling Java files...
echo.

REM Compile all Java files
javac -encoding UTF-8 -d target\classes -sourcepath src\main\java -cp "lib\*" src\main\java\Register\*.java src\main\java\Utils\*.java src\main\java\Database\*.java src\main\java\Admin\*.java src\main\java\Peminjam\*.java src\main\java\com\mycompany\sisteminventarisukm\*.java

if %errorlevel% neq 0 (
    echo.
    echo ========================================
    echo ERROR: Compilation failed!
    echo ========================================
    pause
    exit /b 1
)

echo.
echo ========================================
echo Build successful!
echo ========================================
echo.
echo To run the application, use: run.bat
echo.
pause
