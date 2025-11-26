# Script untuk update semua file Admin dan Peminjam
# Menambahkan fullscreen dan navigasi lengkap

$adminFiles = @("hapusbarang", "LogPeminjaman", "RequestPeminjaman", "RequestPengembalian")
$peminj amFiles = @("DashboardPeminjam", "RiwayatPeminjam", "ProfilPeminjam", "DetailBarang", "DetailRiwayat", "HalamanPengembalian")

Write-Host "Update akan dilakukan manual melalui editor untuk memastikan kode benar" -ForegroundColor Yellow
Write-Host ""
Write-Host "Files yang perlu diupdate:" -ForegroundColor Cyan
Write-Host "Admin:" -ForegroundColor Green
$adminFiles | ForEach-Object { Write-Host "  - $_" }
Write-Host "Peminjam:" -ForegroundColor Green
$peminj amFiles | ForEach-Object { Write-Host "  - $_" }
