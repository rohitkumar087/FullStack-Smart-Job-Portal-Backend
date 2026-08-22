package com.jobportal.controller;

import com.jobportal.entity.*;
import com.jobportal.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/recruiters/pending")
    public List<CompanyProfile> getPendingRecruiters() {

        return adminService.getPendingRecruiters();
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/updateRecruiterStatus/{id}")
    public CompanyProfile updateRecruiterStatus(
            @PathVariable long id,
            @RequestParam RecruiterStatus status) {

        return adminService.updateRecruiterStatus(id, status);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getAllRecruiters")
    public List<CompanyProfile> getAllRecruiters() {

        return adminService.getAllRecruiters();
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getAllCandidates")
    public List<User> getAllCandidates() {

        return adminService.getAllCandidates();
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getAllUsers")
    public List<User> getAllUsers() {

        return adminService.getAllUsers();
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getAllJobs")
    public List<Job> getAllJobs() {

        return adminService.getAllJobs();
    }

}
