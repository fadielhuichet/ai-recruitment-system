package com.fedicode.authenticationservice.Feign;

import com.fedicode.authenticationservice.model.Admin;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@FeignClient(name = "admin-service")
public interface AdminServiceRestClient {

    @GetMapping("/admin/{adminEmail}")
    Optional<Admin> findByEmail(@PathVariable String adminEmail);

}
