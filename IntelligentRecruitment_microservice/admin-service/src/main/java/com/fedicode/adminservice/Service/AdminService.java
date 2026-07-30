package com.fedicode.adminservice.Service;

import com.fedicode.adminservice.Entity.Admin;
import com.fedicode.adminservice.Repository.AdminRepository;
import com.fedicode.adminservice.dto.AdminChangeInfoRequest;
import com.fedicode.adminservice.dto.AdminChangePasswordRequest;
import com.fedicode.adminservice.dto.AdminResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class AdminService {

    private AdminRepository adminRepository;
    private PasswordEncoder passwordEncoder;

    public Optional<Admin> findByEmail(String email){
        return adminRepository.findByEmail(email);
    }


    public AdminResponse getAdminInfo(String email) {
        Admin admin=adminRepository.findByEmail(email)
                .orElseThrow(()->new RuntimeException("Admin does not exist"));
        return AdminResponse.builder()
                .id(admin.getId())
                .username(admin.getUsername())
                .email(admin.getEmail())
                .phone(admin.getPhone())
                .createdAt(admin.getCreatedAt())
                .build();
    }

    public void changeInformation(AdminChangeInfoRequest request, String adminEmail) {
        Admin admin = adminRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new RuntimeException("Admin does not exist"));

        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            admin.setUsername(request.getUsername().trim());
        }
        if (request.getPhone() != null && !request.getPhone().isBlank()) {
            admin.setPhone(request.getPhone().trim());
        }
        if (request.getEmail() != null && !request.getEmail().isBlank()
                && !request.getEmail().equalsIgnoreCase(admin.getEmail())) {
            if (adminRepository.existsByEmail(request.getEmail())) {
                throw new IllegalArgumentException("Email already exists");
            }
            admin.setEmail(request.getEmail().trim());
        }

        adminRepository.save(admin);
    }

    public void changePassword(AdminChangePasswordRequest request, String adminEmail) {
        Admin admin = adminRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new RuntimeException("Admin does not exist"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), admin.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        admin.setPassword(passwordEncoder.encode(request.getNewPassword()));
        adminRepository.save(admin);
    }
}
