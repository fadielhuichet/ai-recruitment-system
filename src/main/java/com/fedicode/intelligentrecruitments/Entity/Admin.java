package com.fedicode.intelligentrecruitments.Entity;

import jakarta.persistence.*;

@Entity
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    String email;
    String password;
    String phone;
    String firstName;
    String lastName;

}
