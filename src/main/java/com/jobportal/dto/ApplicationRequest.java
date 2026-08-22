package com.jobportal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ApplicationRequest {

    @NotBlank(message = "Phone number is required")
    private String phone;

    @NotBlank(message = "Current location is required")
    private String currentLocation;

    @NotBlank(message = "Experience is required")
    private String experience;

    @NotNull(message = "Expected salary is required")
    private Double expectedSalary;

    private String coverLetter;

    @NotBlank(message = "Portfolio URL is required")
    private String portfolioUrl;

    @NotBlank(message = "LinkedIn URL is required")
    private String linkedinUrl;

    @NotBlank(message = "GitHub URL is required")
    private String githubUrl;

    @NotEmpty(message = "At least one skill is required")
    private List<String> skills;
}
