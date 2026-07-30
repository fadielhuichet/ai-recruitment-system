package com.fedicode.recruiterservice.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecruiterEmailConfig {
    @Id
    private String recruiterEmail; // PK = recruiter identity

    private String acceptSubject;
    @Column(columnDefinition = "TEXT")
    private String acceptBody;

    private String refuseSubject;
    @Column(columnDefinition = "TEXT")
    private String refuseBody;
}
