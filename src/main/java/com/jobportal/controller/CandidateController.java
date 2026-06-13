package com.jobportal.controller;

import com.jobportal.entity.Candidate;
import com.jobportal.service.CandidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/candidate")
public class CandidateController {
    @Autowired
    private CandidateService candidateService;

    @PreAuthorize("hasRole('CANDIDATE')")
    @GetMapping("/profile")
    public Candidate getMyProfile(Authentication authentication){
        String email = authentication.getName();
        return candidateService.getMyProfile(email);
    }

    @PreAuthorize("hasRole('CANDIDATE')")
    @PutMapping("profile")
    public Candidate updateProfile(@RequestBody Candidate updatedCandidate,Authentication authentication){
        String email = authentication.getName();
        return candidateService.updateMyProfile(email,updatedCandidate);
    }

}
