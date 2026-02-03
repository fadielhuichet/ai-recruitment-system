package com.fedicode.intelligentrecruitments.Entity;

import io.hypersistence.utils.hibernate.type.array.ListArrayType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.sql.SQLType;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType. LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    // Informations candidat
    @Column(name = "candidate_name", nullable = false)
    private String candidateName;

    @Column(name = "candidate_email", nullable = false)
    private String candidateEmail;

    @Column(name = "candidate_phone")
    private String candidatePhone;

    // CV
    @Column(name = "cv_file_path", nullable = false)
    private String cvFilePath;

    @Column(name = "cv_text", columnDefinition = "TEXT", nullable = false)
    private String cvText;

    // Résultats LLM
    @Column(name = "llm_score", precision = 5, scale = 2)
    private BigDecimal llmScore;  // Ex: 85.50

    @Column(name = "llm_analysis", columnDefinition = "TEXT")
    private String llmAnalysis;

//    @Type(ListArrayType.class)
    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "llm_strengths", columnDefinition = "TEXT[]")
    private List<String> llmStrengths;

//    @Type(ListArrayType.class)
    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "llm_weaknesses", columnDefinition = "TEXT[]")
    private List<String> llmWeaknesses;

    // Statut
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status = ApplicationStatus.PENDING;

    @Column(name = "analyzed_at")
    private LocalDateTime analyzedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}

enum ApplicationStatus {
    PENDING,   // En attente d'analyse
    ANALYZING, // En cours d'analyse
    ANALYZED,  // Analysé
    REVIEWED   // Consulté par le recruteur
}
