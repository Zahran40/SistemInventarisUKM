# Script untuk compile semua file Java
Write-Host "Cleaning old compiled files..." -ForegroundColor Yellow
Remove-Item -Path "target\classes\*" -Recurse -Force -ErrorAction SilentlyContinue

Write-Host "Creating directories..." -ForegroundColor Yellow
New-Item -ItemType Directory -Force -Path "target\classes" | Out-Null

Write-Host "Compiling Java files..." -ForegroundColor Yellow
$env:CLASSPATH = ".;mysql-connector-j-8.0.33.jar"

# Compile in correct order
cd src\main\java

# 1. Compile Utils first
javac -encoding UTF-8 -d ..\..\..\target\classes Utils\*.java
if ($LASTEXITCODE -ne 0) { Write-Host "Utils compilation failed!" -ForegroundColor Red; exit 1 }

# 2. Compile Database
javac -encoding UTF-8 -d ..\..\..\target\classes -cp ".;..\..\..\mysql-connector-j-8.0.33.jar;..\..\..\target\classes" Database\*.java
if ($LASTEXITCODE -ne 0) { Write-Host "Database compilation failed!" -ForegroundColor Red; exit 1 }

# 3. Compile Model
javac -encoding UTF-8 -d ..\..\..\target\classes -cp ".;..\..\..\mysql-connector-j-8.0.33.jar;..\..\..\target\classes" Model\*.java
if ($LASTEXITCODE -ne 0) { Write-Host "Model compilation failed!" -ForegroundColor Red; exit 1 }

# 4. Compile DAO
javac -encoding UTF-8 -d ..\..\..\target\classes -cp ".;..\..\..\mysql-connector-j-8.0.33.jar;..\..\..\target\classes" DAO\*.java
if ($LASTEXITCODE -ne 0) { Write-Host "DAO compilation failed!" -ForegroundColor Red; exit 1 }

# 5. Compile Register
javac -encoding UTF-8 -d ..\..\..\target\classes -cp ".;..\..\..\mysql-connector-j-8.0.33.jar;..\..\..\target\classes" Register\*.java
if ($LASTEXITCODE -ne 0) { Write-Host "Register compilation failed!" -ForegroundColor Red; exit 1 }

# 6. Compile Admin (skip files with errors)
Write-Host "Compiling Admin files..." -ForegroundColor Cyan
Get-ChildItem Admin\*.java | ForEach-Object {
    $file = $_.FullName
    javac -encoding UTF-8 -d ..\..\..\target\classes -cp ".;..\..\..\mysql-connector-j-8.0.33.jar;..\..\..\target\classes" $file 2>&1 | Out-Null
    if ($LASTEXITCODE -eq 0) {
        Write-Host "  ✓ $($_.Name)" -ForegroundColor Green
    } else {
        Write-Host "  ✗ $($_.Name)" -ForegroundColor Red
    }
}

# 7. Compile Peminjam (skip files with errors)
Write-Host "Compiling Peminjam files..." -ForegroundColor Cyan
Get-ChildItem Peminjam\*.java | ForEach-Object {
    $file = $_.FullName
    javac -encoding UTF-8 -d ..\..\..\target\classes -cp ".;..\..\..\mysql-connector-j-8.0.33.jar;..\..\..\target\classes" $file 2>&1 | Out-Null
    if ($LASTEXITCODE -eq 0) {
        Write-Host "  ✓ $($_.Name)" -ForegroundColor Green
    } else {
        Write-Host "  ✗ $($_.Name)" -ForegroundColor Red
    }
}

# 8. Compile main class
javac -encoding UTF-8 -d ..\..\..\target\classes -cp ".;..\..\..\mysql-connector-j-8.0.33.jar;..\..\..\target\classes" com\mycompany\sisteminventarisukm\*.java

cd ..\..\..

Write-Host "`nCompilation completed!" -ForegroundColor Green
Write-Host "You can now run the application from NetBeans or use:" -ForegroundColor Yellow
Write-Host "java -cp 'target/classes;mysql-connector-j-8.0.33.jar' com.mycompany.sisteminventarisukm.Sisteminventarisukm" -ForegroundColor Cyan
