package com.fedicode.applicationservice.Entity;

import com.fedicode.applicationservice.model.Candidate;
import com.fedicode.applicationservice.model.Job;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder

public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int candidateId;
    @Transient
    private Candidate candidate;

    private int jobId;
    @Transient
    private Job job;
    // Informations candidat
    @Column( nullable = false)
    private String candidateFirstName;

    @Column(nullable = false)
    private String candidateLastName;

    @Column( nullable = false)
    private String candidateEmail;

    private String candidatePhone;
    private LocalDate dateOfBirth;

    private LocalDateTime codeExpiration;
    private String verificationCode;

    // CV
    @Column( nullable = false)
    private String cvFilePath;

    @Column(name = "cv_text", columnDefinition = "TEXT", nullable = false)
    private String cvText;

    // Résultats LLM
    @Column(name = "llm_score", precision = 5, scale = 2)
    private BigDecimal llmScore;

    @Column(name = "llm_analysis", columnDefinition = "TEXT")
    private String llmAnalysis;

    // Statut
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status = ApplicationStatus.PENDING;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;


    public static enum ApplicationStatus {
        PENDING,   // En attente d'analyse
        ANALYZING, // En cours d'analyse
        ANALYZED,  // Analysé
        REVIEWED,
        ACCEPTED,
        REFUSED// Consulté par le recruteur
    }

}

