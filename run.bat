@echo off
REM Script untuk menjalankan aplikasi

echo ========================================
echo Sistem Inventaris UKM
echo ========================================
echo.

REM Check if compiled
if not exist "target\classes\Register\LoginPage.class" (
    echo Files not compiled yet!
    echo Please run build.bat first.
    echo.
    pause
    exit /b 1
)

REM Check if MySQL Connector exists
if not exist "lib\mysql-connector-j-8.0.33.jar" (
    echo MySQL JDBC Driver not found!
    echo Please run build.bat first.
    echo.
    pause
    exit /b 1
)

echo Starting application...
echo.
echo Login Credentials:
echo ------------------
echo Admin:
echo   Username: ADM001
echo   Password: password123
echo.
echo Peminjam:
echo   Username: 2101001
echo   Password: password123
echo.
echo FITUR BARU:
echo - Semua halaman FULLSCREEN otomatis
echo - Navigasi tombol berfungsi lengkap
echo - Login dengan NIM atau Email
echo ========================================
echo.

REM Run application
java -cp "target\classes;lib\*" Register.LoginPage

echo.
echo Application closed.
pause
