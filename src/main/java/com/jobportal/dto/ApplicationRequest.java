package com.jobportal.dto;

import lombok.Data;

import java.util.List;

@Data
public class ApplicationRequest {

    private String phone;

    private String currentLocation;

    private String experience;

    private Double expectedSalary;

    private String coverLetter;

    private String portfolioUrl;

    private String linkedinUrl;

    private String githubUrl;

    private List<String> skills;
}
