package com.fedicode.adminservice.Repository;

import com.fedicode.adminservice.Entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin,Integer> {

    Optional<Admin> findByEmail(String Email);

    boolean existsByEmail(String email);
}
