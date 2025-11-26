# Script PowerShell untuk menjalankan aplikasi

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Sistem Inventaris UKM" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Check if compiled
if (-not (Test-Path "target\classes\Register\LoginPage.class")) {
    Write-Host "Files not compiled yet!" -ForegroundColor Red
    Write-Host "Please run build.ps1 first." -ForegroundColor Yellow
    Write-Host ""
    exit 1
}

# Check if MySQL Connector exists
if (-not (Test-Path "lib\mysql-connector-j-8.0.33.jar")) {
    Write-Host "MySQL JDBC Driver not found!" -ForegroundColor Red
    Write-Host "Please run build.ps1 first." -ForegroundColor Yellow
    Write-Host ""
    exit 1
}

Write-Host "Starting application..." -ForegroundColor Yellow
Write-Host ""
Write-Host "Login Credentials:" -ForegroundColor Cyan
Write-Host "------------------" -ForegroundColor Cyan
Write-Host "Admin:" -ForegroundColor Green
Write-Host "  Username: ADM001" -ForegroundColor White
Write-Host "  Password: password123" -ForegroundColor White
Write-Host ""
Write-Host "Peminjam:" -ForegroundColor Green
Write-Host "  Username: 2101001" -ForegroundColor White
Write-Host "  Password: password123" -ForegroundColor White
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Run application
java -cp "target\classes;lib\*" Register.LoginPage

Write-Host ""
Write-Host "Application closed." -ForegroundColor Yellow
