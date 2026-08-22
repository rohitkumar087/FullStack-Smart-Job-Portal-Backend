package com.jobportal.service;

import com.jobportal.entity.*;
import com.jobportal.repository.CompanyProfileRepository;
import com.jobportal.repository.JobRepository;
import com.jobportal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    @Autowired
    private CompanyProfileRepository companyProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobRepository jobRepository;


    // Get all recruiters whose verification status is PENDING
    public List<CompanyProfile> getPendingRecruiters() {

        return companyProfileRepository.findByRecruiterStatus(RecruiterStatus.PENDING);
    }


    // Update recruiter/company verification status
    public CompanyProfile updateRecruiterStatus(
            long id,
            RecruiterStatus status) {

        CompanyProfile companyProfile = companyProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company profile not found"));

        companyProfile.setRecruiterStatus(status);

        return companyProfileRepository.save(companyProfile);
    }


    // Get all recruiters/company profiles
    public List<CompanyProfile> getAllRecruiters() {

        return companyProfileRepository.findAll();
    }

    // Get all candidates
    public List<User> getAllCandidates() {
        return userRepository.findByRole(Role.CANDIDATE);
    }

    // Get all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Get all jobs
    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }
}