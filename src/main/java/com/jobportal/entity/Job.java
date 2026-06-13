package com.jobportal.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    @Column(length = 1000)
    private String description;

    @NotBlank(message = "Location is required")
    private String location;

    private Double minSalary;

    private Double maxSalary;

    @NotBlank(message = "Company is required")
    private String company;

    private String jobType;

    private String experience;

    private Integer openings;

    @Enumerated(EnumType.STRING)
    private JobStatus status;

    @ElementCollection
    private List<String> skills = new ArrayList<>();

    @ElementCollection
    @Column(length = 1000)
    private List<String> responsibilities = new ArrayList<>();

    @ElementCollection
    @Column(length = 1000)
    private List<String> requirements = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "recruiter_id")
    private User recruiter;

    private LocalDateTime createdAt;

}
