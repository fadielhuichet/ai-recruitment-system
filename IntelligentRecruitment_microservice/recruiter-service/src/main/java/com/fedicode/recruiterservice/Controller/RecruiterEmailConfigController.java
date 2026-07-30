package com.fedicode.recruiterservice.Controller;

import com.fedicode.recruiterservice.Dto.EmailConfigDto;
import com.fedicode.recruiterservice.Service.RecruiterEmailConfigService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/email-config")
@AllArgsConstructor
public class RecruiterEmailConfigController {

    private final RecruiterEmailConfigService service;


    @GetMapping
    public ResponseEntity<EmailConfigDto> getMyConfig(
            @RequestHeader("X-User-Email") String recruiterEmail) {
        return ResponseEntity.ok(service.getConfig(recruiterEmail));
    }


    @PutMapping
    public ResponseEntity<Void> updateMyConfig(
            @RequestHeader("X-User-Email") String recruiterEmail,
            @RequestBody EmailConfigDto dto) {
        service.saveConfig(recruiterEmail, dto);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/internal/{recruiterEmail}")
    public ResponseEntity<EmailConfigDto> getConfigInternal(
            @PathVariable String recruiterEmail) {
        return ResponseEntity.ok(service.getConfig(recruiterEmail));
    }
}