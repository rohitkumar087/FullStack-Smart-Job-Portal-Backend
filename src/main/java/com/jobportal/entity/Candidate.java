package com.jobportal.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Candidate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    private String phone;

    private String location;

    private String title;

    private String experience;

    private Double expectedSalary;

    private String resumeUrl;

    private String portfolioUrl;

    private String linkedinUrl;

    private String githubUrl;

    @ElementCollection
    private List<String> skills = new ArrayList<>();
}
