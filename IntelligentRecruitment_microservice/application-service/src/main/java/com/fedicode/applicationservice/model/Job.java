package com.fedicode.applicationservice.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Job {

    private int id;
    private int recruiterId;
    private JobStatus status;
    private String title;
    private String company;
    private String description;
    private String location;
    private Recruiter recruiter;
    private LocalDateTime createdAt;


}
