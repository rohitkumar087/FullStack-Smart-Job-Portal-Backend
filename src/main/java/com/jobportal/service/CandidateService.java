package com.jobportal.service;

import com.jobportal.entity.Candidate;
import com.jobportal.entity.User;
import com.jobportal.exception.UserNotFoundException;
import com.jobportal.repository.CandidateRepository;
import com.jobportal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;

@Service
public class CandidateService {

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private UserRepository userRepository;

    public Candidate getMyProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User Not Found"));

        return candidateRepository.findByUser(user)
                .orElseGet(()->{
                    Candidate candidate = new Candidate();

                    candidate.setUser(user);
                    candidate.setPhone("");
                    candidate.setLocation("");
                    candidate.setTitle("");
                    candidate.setExperience("");
                    candidate.setExpectedSalary(null);
                    candidate.setResumeUrl("");
                    candidate.setPortfolioUrl("");
                    candidate.setLinkedinUrl("");
                    candidate.setGithubUrl("");
                    candidate.setSkills(new ArrayList<>());

                    return candidateRepository.save(candidate);
                });
    }

    public Candidate updateMyProfile(String email, Candidate updatedCandidate) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Candidate candidate = candidateRepository.findByUser(user)
                .orElse(new Candidate());

        candidate.setUser(user);
        candidate.setPhone(updatedCandidate.getPhone());
        candidate.setLocation(updatedCandidate.getLocation());
        candidate.setTitle(updatedCandidate.getTitle());
        candidate.setExperience(updatedCandidate.getExperience());
        candidate.setExpectedSalary(updatedCandidate.getExpectedSalary());
        candidate.setResumeUrl(updatedCandidate.getResumeUrl());
        candidate.setPortfolioUrl(updatedCandidate.getPortfolioUrl());
        candidate.setLinkedinUrl(updatedCandidate.getLinkedinUrl());
        candidate.setGithubUrl(updatedCandidate.getGithubUrl());
        candidate.setSkills(updatedCandidate.getSkills());

        return candidateRepository.save(candidate);
    }
}
