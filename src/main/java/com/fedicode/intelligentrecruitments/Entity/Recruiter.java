package com.fedicode.intelligentrecruitments.Entity;


import jakarta.persistence.*;
import lombok.*;

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

    @Column(nullable = false,unique = true)
    private String email;
    @Column(nullable = false,unique = true)
    private String password;

    private String companyName;
    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    @Column(nullable = false)
    private String phone;

    @OneToMany(mappedBy = "recruiter")
    private List<Job> jobs;

}
