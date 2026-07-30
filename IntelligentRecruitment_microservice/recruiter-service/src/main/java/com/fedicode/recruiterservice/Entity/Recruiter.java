package com.fedicode.recruiterservice.Entity;


import com.fedicode.recruiterservice.model.Job;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.*;
import org.apache.hc.core5.reactor.IOSession;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.CurrentTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@ToString
public class Recruiter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String companyName;
    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    @Column(nullable = false)
    private String phone;
    @Column(name = "profile_image")
    private String profileImage;
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    private String verificationCode;
    private LocalDateTime codeExpiration;

    @Transient
    private List<Job> jobs;
    @Enumerated(EnumType.STRING)
    private Status status= Status.ACTIVE;
    @Enumerated(EnumType.STRING)
    private Role role = Role.RECRUITER;

    private LocalDateTime suspendedAt;
    private String suspendedBy;
    private String suspensionReason;

}


