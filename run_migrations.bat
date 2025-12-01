@echo off
echo Running database migrations...
echo.

mysql -u root -p sistem_inventaris_ukm < migration_add_bukti_validasi.sql
echo [OK] Added bukti_validasi column to peminjaman
echo.

mysql -u root -p sistem_inventaris_ukm < migration_update_barang_status.sql
echo [OK] Updated barang status enum (added 'dipinjam')
echo.

mysql -u root -p sistem_inventaris_ukm < migration_add_bukti_kembali.sql
echo [OK] Added bukti_kembali column to pengembalian
echo.

echo Migration complete!
pause
