package com.fedicode.applicationservice.Service;

import com.fedicode.applicationservice.Entity.Application;
import com.fedicode.applicationservice.Repository.ApplicationRepository;
import org.springframework.core.io.Resource;
import jakarta.validation.constraints.NotBlank;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.print.DocFlavor;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileService {

    @Value("${file.upload-dir}")
    private String uploadDir;
    private final ApplicationRepository applicationRepository;

    public FileService(ApplicationRepository applicationRepository){
        this.applicationRepository=applicationRepository;
    }

    public String extractText(String cvFilePath) {
        try {
            Path fullPath = Paths.get(uploadDir).resolve(cvFilePath);
            PDDocument document = PDDocument.load(fullPath.toFile());
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            document.close();
            return text;
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de l'extraction du texte PDF : " + e.getMessage());
        }
    }

    public ResponseEntity<Resource> getPdfFromFolder(int applicationId) {
        try {
            Application application = applicationRepository.findById(applicationId)
                    .orElseThrow(() -> new RuntimeException("Application not found"));

            Path filePath = Paths.get(uploadDir).resolve(application.getCvFilePath());

            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_PDF)
                        .header(
                                HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=\"" + resource.getFilename() + "\""
                        )
                        .body(resource);
            }

            return ResponseEntity.notFound().build();

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}