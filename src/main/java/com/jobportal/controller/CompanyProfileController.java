package com.jobportal.controller;

import com.jobportal.entity.CompanyProfile;
import com.jobportal.entity.User;
import com.jobportal.repository.CompanyProfileRepository;
import com.jobportal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/company")
public class CompanyProfileController {

    @Autowired
    private CompanyProfileRepository companyProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @PreAuthorize("hasRole('RECRUITER')")
    @GetMapping("/profile")
    public CompanyProfile getMyCompanyProfile(Authentication authentication) {

        String email = authentication.getName();

        User recruiter = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Recruiter not found"));

        return companyProfileRepository.findByRecruiter(recruiter)
                .orElseThrow(() -> new RuntimeException("Company profile not found"));
    }
}