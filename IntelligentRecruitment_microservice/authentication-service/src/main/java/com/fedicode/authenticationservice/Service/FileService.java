package com.fedicode.authenticationservice.Service;

import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileService {
    @Value("${file.upload-dir}")
    private String uploadDir;

    public String saveFile(@NotBlank MultipartFile cv) {
        try {
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            String fileName = UUID.randomUUID() + "_" + cv.getOriginalFilename();

            Path filePath = Paths.get(uploadDir,fileName);
            Files.copy(cv.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return filePath.toString();

        } catch (Exception e) {
            throw new RuntimeException("erreur lors de la sauvegarde du fichier: " + e.getMessage());
        }
    }

}
