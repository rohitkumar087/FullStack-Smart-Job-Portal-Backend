package com.jobportal.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class CompanyProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String companyName;
    private String location;
    private String website;

    @Column(length = 1500)
    private String description;

    @Enumerated(EnumType.STRING)
    private RecruiterStatus recruiterStatus;

    @OneToOne
    private User recruiter;
}
