package com.jobportal.controller;

import com.jobportal.entity.Job;
import com.jobportal.service.JobService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.access.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/api/job")
public class JobController {

    @Autowired
    private JobService jobService;

    @PreAuthorize("hasRole('RECRUITER')")
    @PostMapping("/createJob")
    public Job createJob(@Valid @RequestBody Job job, Authentication authentication) {
        String email = authentication.getName();
        System.out.println(authentication.getAuthorities());

        return jobService.createJob(job, email);
    }

    @GetMapping("/getJob")
    public Page<Job> getJob(Pageable pageable) {
        return jobService.getJob(pageable);
    }

    @GetMapping("/{id}")
    public Job getJobById(@PathVariable Long id) {
        return jobService.getJobById(id);
    }

    @PreAuthorize("hasRole('RECRUITER')")
    @DeleteMapping("/{id}")
    public String deleteJob(@PathVariable Long id, Authentication authentication) throws AccessDeniedException {

        String email = authentication.getName();
        jobService.deleteJob(id, email);
        return "Job deleted successfully";
    }

    @PreAuthorize("hasRole('RECRUITER')")
    @GetMapping("/myJob")
    public List<Job> getMyJob(Authentication authentication) {
        String email = authentication.getName();
        return jobService.getMyJob(email);
    }

    @PreAuthorize("hasRole('RECRUITER')")
    @PutMapping("/{id}")
    public Job updateJob(@PathVariable Long id, @Valid @RequestBody Job updatedJob, Authentication authentication) {
        String email = authentication.getName();
        return jobService.updateJob(id, updatedJob, email);
    }

    @GetMapping("/filter")
    public Page<Job> filterJobs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String jobType,
            @RequestParam(required = false) String experience,
            @RequestParam(required = false) Double minSalary,
            @RequestParam(required = false) Double maxSalary,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return jobService.filterJobs(
                keyword,
                location,
                jobType,
                experience,
                minSalary,
                maxSalary,
                page,
                size
        );
    }

//    @GetMapping("/filter")
//    public Page<Job> filterJobs(
//            @RequestParam(required = false) String keyword,
//            @RequestParam(required = false) String location,
//            @RequestParam(required = false) String jobType,
//            @RequestParam(required = false) String experience,
//            Pageable pageable
//    ) {
//        return jobService.filterJobs(keyword, location, jobType, experience, pageable);
//    }

}

