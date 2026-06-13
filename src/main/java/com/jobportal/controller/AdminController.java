package com.jobportal.controller;

import com.jobportal.entity.*;
import com.jobportal.repository.CompanyProfileRepository;
import com.jobportal.repository.JobRepository;
import com.jobportal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private CompanyProfileRepository companyProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobRepository jobRepository;


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/recruiters/pending")
    public List<CompanyProfile> getPendingRecruiters(){
        return companyProfileRepository.findByRecruiterStatus(RecruiterStatus.PENDING);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/updateRecruiterStatus/{id}")
    public CompanyProfile updateRecruiterStatus(@PathVariable long id, @RequestParam RecruiterStatus status){
        CompanyProfile companyProfile = companyProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company profile not found"));

        companyProfile.setRecruiterStatus(status);
        return companyProfileRepository.save(companyProfile);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getAllRecruiters")
    public List<CompanyProfile> getAllRecruiters(){
        return companyProfileRepository.findAll();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getAllCandidates")
    public List<User> getAllCandidates(){
        return userRepository.findByRole(Role.CANDIDATE);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getAllUsers")
    public List<User> Users(){
        return userRepository.findAll();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getAllJobs")
    public List<Job> Jobs(){
        return jobRepository.findAll();
    }

}
