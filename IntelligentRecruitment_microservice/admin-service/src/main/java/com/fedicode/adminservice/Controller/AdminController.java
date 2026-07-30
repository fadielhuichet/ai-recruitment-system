package com.fedicode.adminservice.Controller;

import com.fedicode.adminservice.Entity.Admin;
import com.fedicode.adminservice.Service.AdminService;
import com.fedicode.adminservice.dto.AdminChangeInfoRequest;
import com.fedicode.adminservice.dto.AdminChangePasswordRequest;
import com.fedicode.adminservice.dto.AdminResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/admin")
@AllArgsConstructor
public class AdminController {

    private AdminService adminService;

    @GetMapping("/{adminEmail}")
    public Optional<Admin> findByEmail(@PathVariable String adminEmail){
        return adminService.findByEmail(adminEmail);
    }

    @GetMapping("/admin-info")
    public AdminResponse getAdminInfo(@RequestHeader("X-User-Email")String email){
        return adminService.getAdminInfo(email);
    }

    @PatchMapping("/account-info")
    public void changeAccountInfo(
            @RequestBody AdminChangeInfoRequest request,
            @RequestHeader("X-User-Email") String email
    ) {
        adminService.changeInformation(request, email);
    }

    @PatchMapping("/changePassword")
    public void changePassword(
            @RequestBody AdminChangePasswordRequest request,
            @RequestHeader("X-User-Email") String email
    ) {
        adminService.changePassword(request, email);
    }

}
