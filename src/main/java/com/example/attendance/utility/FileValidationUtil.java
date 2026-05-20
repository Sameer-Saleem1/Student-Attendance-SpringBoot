package com.example.attendance.utility;

import com.example.attendance.config.AppConstants;
import com.example.attendance.exception.ValidationException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileValidationUtil {

    public void validateUpload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ValidationException("Please select a CSV file to upload.");
        }
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.toLowerCase().endsWith(AppConstants.CSV_EXTENSION)) {
            throw new ValidationException("Only .csv files are allowed.");
        }
    }
}
