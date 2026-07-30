package com.fedicode.jobservice.Entity;

import com.fedicode.jobservice.model.Application;
import com.fedicode.jobservice.model.Recruiter;

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

    private int recruiterId;


    private String title;

    @Column(nullable = false,columnDefinition = "TEXT")
    private String description;
    @Column(nullable = false)
    private String company;

    @Column(nullable = false)
    private String location;

    @Column(name = "application_link", unique = true, nullable = false)
    private String applicationLink;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobStatus status= JobStatus.ACTIVE;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private JobCategory category;

    private String customCategory;


    @Transient
    public int getApplicationCount(){
        return applications != null ? applications.size() : 0;
    }
    @Transient
    private Recruiter recruiter;

    @Transient
    private List<Application> applications;

}
