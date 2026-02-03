package com.fedicode.intelligentrecruitments.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name="recruiter_id",nullable = false)
    private Recruiter recruiter;

    private String title;

    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private String company;

    @Column(nullable = false)
    private String location;

    @Column(name = "application_link", unique = true, nullable = false)
    private String applicationLink;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobStatus status=JobStatus.ACTIVE;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "job")
    private List<Application> applications;

    @Transient
    public int getApplicationCount(){
        return applications != null ? applications.size() : 0;
    }

}
enum JobStatus {
    ACTIVE,CLOSED

}