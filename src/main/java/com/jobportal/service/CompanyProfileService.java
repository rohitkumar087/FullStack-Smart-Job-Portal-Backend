package com.jobportal.service;

import com.jobportal.entity.CompanyProfile;
import com.jobportal.entity.User;
import com.jobportal.exception.UserNotFoundException;
import com.jobportal.repository.CompanyProfileRepository;
import com.jobportal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CompanyProfileService {

    @Autowired
    private CompanyProfileRepository companyProfileRepository;

    @Autowired
    private UserRepository userRepository;

    public CompanyProfile getMyCompanyProfile(String email){
        User recruiter = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Recruiter not found"));

        return companyProfileRepository.findByRecruiter(recruiter)
                .orElseThrow(() -> new RuntimeException("Company profile not found"));
    }
}
