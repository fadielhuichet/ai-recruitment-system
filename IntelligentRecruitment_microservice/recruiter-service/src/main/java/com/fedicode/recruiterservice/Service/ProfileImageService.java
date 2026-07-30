package com.fedicode.recruiterservice.Service;

import com.fedicode.recruiterservice.Entity.Recruiter;
import com.fedicode.recruiterservice.Repository.RecruiterRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ProfileImageService {

    private final RecruiterRepository recruiterRepository;

    public String saveImage(MultipartFile file, String recruiterEmail, String uploadDir)
            throws IOException {

        Recruiter recruiter = recruiterRepository.findByEmail(recruiterEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Recruiter not found"));

        // delete old image if exists
        if (recruiter.getProfileImage() != null) {
            try {
                String oldFilename = recruiter.getProfileImage()
                        .substring(recruiter.getProfileImage().lastIndexOf("/") + 1);
                Path oldPath = Paths.get(uploadDir).resolve(oldFilename);
                Files.deleteIfExists(oldPath);
            } catch (Exception ignored) {}
        }

        // save new image
        String extension = getExtension(file.getOriginalFilename());
        String filename = UUID.randomUUID() + "." + extension;

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        String imageUrl = "/recruiter/profile-image/" + filename;
        recruiter.setProfileImage(imageUrl);
        recruiterRepository.save(recruiter);

        return imageUrl;
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "jpg";
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }
}