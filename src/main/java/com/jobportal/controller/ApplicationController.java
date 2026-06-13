package com.jobportal.controller;

import com.jobportal.dto.ApplicationRequest;
import com.jobportal.entity.Application;
import com.jobportal.entity.ApplicationStatus;
import com.jobportal.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/api/application")
public class ApplicationController {
    @Autowired
    private ApplicationService applicationService;


    @PreAuthorize("hasRole('CANDIDATE')")
    @PostMapping(value = "/apply/{jobId}",consumes = "multipart/form-data")
    public String applyJob(@PathVariable Long jobId, @ModelAttribute ApplicationRequest request,@RequestParam("resume") MultipartFile resume , Authentication authentication) throws IOException {
        String email = authentication.getName();
        return applicationService.applyJob(jobId,email,request,resume);
    }

    @PreAuthorize("hasRole('CANDIDATE')")
    @GetMapping("/myApplications")
    public List<Application> getApplyJob(Authentication authentication){
        String email = authentication.getName();
        return applicationService.getAppliedJob(email);
    }

    @PreAuthorize("hasRole('RECRUITER')")
    @GetMapping("/appliedApplicants/{jobId}")
    public List<Application> getApplicants(@PathVariable Long jobId,Authentication authentication) throws AccessDeniedException {
        String email = authentication.getName();
        return applicationService.getApplicants(jobId,email);
    }

    @PreAuthorize("hasRole('RECRUITER')")
    @PutMapping("/updateStatus/{applicationId}/status")
    public String updateStatus(@PathVariable Long applicationId ,@RequestParam ApplicationStatus status, Authentication authentication) throws AccessDeniedException {
        String email = authentication.getName();
        return applicationService.updateApplicationStatus(applicationId,email,status);
    }

    @PreAuthorize("hasRole('CANDIDATE')")
    @PostMapping("/uploadResume/{applicationId}")
    public String uploadResume(@PathVariable Long applicationId, @RequestParam("file")MultipartFile file, Authentication authentication) throws IOException {
        String email = authentication.getName();
        return applicationService.uploadResume(applicationId,file,email);
    }

    @GetMapping("/resume/{applicationId}")
    public ResponseEntity<Resource> downloadResume(@PathVariable Long applicationId) throws IOException {
        return applicationService.downloadResume(applicationId);
    }

}
