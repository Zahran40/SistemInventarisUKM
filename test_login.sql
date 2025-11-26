-- Test query untuk debugging login admin

USE sistem_inventaris_ukm;

-- Cek data admin
SELECT 'Data Admin:' AS info;
SELECT id_user, nama_user, NIM, email, role, LENGTH(NIM) as nim_length, LENGTH(password) as pass_length
FROM users 
WHERE role = 'admin';

-- Cek data peminjam
SELECT 'Data Peminjam:' AS info;
SELECT id_user, nama_user, NIM, email, role, LENGTH(NIM) as nim_length, LENGTH(password) as pass_length
FROM users 
WHERE role = 'peminjam';

-- Test query dengan TRIM untuk admin
SELECT 'Test Query Admin (ADM001):' AS info;
SELECT * FROM users 
WHERE (TRIM(NIM) = TRIM('ADM001') OR TRIM(email) = TRIM('ADM001')) 
AND password = 'password123';

-- Test query dengan TRIM untuk peminjam
SELECT 'Test Query Peminjam (2101001):' AS info;
SELECT * FROM users 
WHERE (TRIM(NIM) = TRIM('2101001') OR TRIM(email) = TRIM('2101001')) 
AND password = 'password123';

-- Cek apakah ada karakter tersembunyi
SELECT 'Check Hidden Characters:' AS info;
SELECT id_user, nama_user, 
       HEX(NIM) as nim_hex, 
       HEX(password) as password_hex,
       role
FROM users;
