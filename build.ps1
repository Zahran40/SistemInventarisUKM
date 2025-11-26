# Script PowerShell untuk compile dan run aplikasi

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Sistem Inventaris UKM - Build Script" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Create lib folder
if (-not (Test-Path "lib")) {
    Write-Host "Creating lib folder..." -ForegroundColor Yellow
    New-Item -ItemType Directory -Path "lib" -Force | Out-Null
}

# Download MySQL Connector if not exists
if (-not (Test-Path "lib\mysql-connector-j-8.0.33.jar")) {
    Write-Host "Downloading MySQL JDBC Driver..." -ForegroundColor Yellow
    Invoke-WebRequest -Uri "https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.0.33/mysql-connector-j-8.0.33.jar" -OutFile "lib\mysql-connector-j-8.0.33.jar"
    Write-Host "Done!" -ForegroundColor Green
    Write-Host ""
}

# Create target/classes folder
if (-not (Test-Path "target\classes")) {
    New-Item -ItemType Directory -Path "target\classes" -Force | Out-Null
}

Write-Host "Compiling Java files..." -ForegroundColor Yellow
Write-Host ""

# Compile all Java files
javac -encoding UTF-8 -d target\classes -sourcepath src\main\java -cp "lib\*" `
    src\main\java\Register\*.java `
    src\main\java\Utils\*.java `
    src\main\java\Database\*.java `
    src\main\java\Admin\*.java `
    src\main\java\Peminjam\*.java `
    src\main\java\com\mycompany\sisteminventarisukm\*.java

if ($LASTEXITCODE -ne 0) {
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Red
    Write-Host "ERROR: Compilation failed!" -ForegroundColor Red
    Write-Host "========================================" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "Build successful!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""
Write-Host "To run the application, use: .\run.ps1" -ForegroundColor Cyan
Write-Host ""
