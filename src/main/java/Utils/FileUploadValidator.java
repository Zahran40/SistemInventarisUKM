package Utils;

import java.io.File;

/**
 * Class untuk validasi file upload
 * Mengikuti prinsip Single Responsibility Principle (SRP)
 */
public class FileUploadValidator {
    
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB dalam bytes
    private static final String[] ALLOWED_EXTENSIONS = {"png", "jpg", "jpeg", "pdf"};
    
    /**
     * Validasi file yang akan diupload
     * @param file File yang akan divalidasi
     * @return ValidationResult object
     */
    public static ValidationResult validate(File file) {
        if (file == null || !file.exists()) {
            return new ValidationResult(false, "File tidak ditemukan!");
        }
        
        // Check ukuran file
        if (file.length() > MAX_FILE_SIZE) {
            double sizeMB = file.length() / (1024.0 * 1024.0);
            return new ValidationResult(false, 
                String.format("Ukuran file terlalu besar! (%.2f MB)\nMaksimal 5 MB.", sizeMB));
        }
        
        // Check ekstensi file
        String fileName = file.getName().toLowerCase();
        boolean validExtension = false;
        
        for (String ext : ALLOWED_EXTENSIONS) {
            if (fileName.endsWith("." + ext)) {
                validExtension = true;
                break;
            }
        }
        
        if (!validExtension) {
            return new ValidationResult(false, 
                "Format file tidak didukung!\nHanya PNG, JPG, JPEG, dan PDF yang diperbolehkan.");
        }
        
        return new ValidationResult(true, "File valid");
    }
    
    /**
     * Inner class untuk menyimpan hasil validasi
     */
    public static class ValidationResult {
        private final boolean valid;
        private final String message;
        
        public ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public String getMessage() {
            return message;
        }
    }
}
