package com.fedicode.candidateservice.Entity;


import com.fedicode.candidateservice.Model.Application;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Candidate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    @Column(nullable = false)
    private LocalDate dateOfBirth;
    @Column(nullable = false,unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String country;


    @Column(name = "profile_image")
    private String profileImage;

    @Column( nullable = false)
    private String cvFilePath;

    @CreationTimestamp
    @Column(name ="created_at" , updatable = false)
    private LocalDateTime createdAt;

    private String verificationCode;
    private LocalDateTime codeExpiration;

    private Role role=Role.CANDIDATE;
    private Status status=Status.ACTIVE;

    private LocalDateTime suspendedAt;
    private String suspensionReason;

    @Transient
    private List<Application> applications;
}
