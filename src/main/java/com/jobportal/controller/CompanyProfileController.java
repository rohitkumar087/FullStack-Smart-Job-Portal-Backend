package com.jobportal.controller;

import com.jobportal.entity.CompanyProfile;
import com.jobportal.service.CompanyProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/company")
public class CompanyProfileController {

    @Autowired
    private CompanyProfileService companyProfileService;

    @PreAuthorize("hasRole('RECRUITER')")
    @GetMapping("/profile")
    public CompanyProfile getMyCompanyProfile(Authentication authentication) {
        String email = authentication.getName();

        return companyProfileService.getMyCompanyProfile(email);
    }
}