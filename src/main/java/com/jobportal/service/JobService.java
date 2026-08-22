package com.jobportal.service;

import com.jobportal.entity.*;
import com.jobportal.exception.JobNotFoundException;
import com.jobportal.exception.UserNotFoundException;
import com.jobportal.repository.CompanyProfileRepository;
import com.jobportal.repository.JobRepository;
import com.jobportal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class JobService {
    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CompanyProfileRepository companyProfileRepository;

    public Job createJob(Job job,String recruiterEmail){
        User recruiter = userRepository.findByEmail(recruiterEmail)
                .orElseThrow(()->new UserNotFoundException("Recruiter not found"));

        job.setRecruiter(recruiter);
        job.setCreatedAt(LocalDateTime.now());
        job.setStatus(JobStatus.ACTIVE);

        CompanyProfile companyProfile = companyProfileRepository.findByRecruiter(recruiter)
                .orElseThrow(() -> new RuntimeException("Company profile not found"));

        if(companyProfile.getRecruiterStatus() != RecruiterStatus.VERIFIED){
            throw  new RuntimeException("Your Company is not verified by admin yet");
        }
        return jobRepository.save(job);
    }

    public Page<Job> getJob(Pageable pageable){
        return jobRepository.findAll(pageable);
    }

    public Job getJobById(Long id){
        return jobRepository.findById(id)
                .orElseThrow(() -> new JobNotFoundException("Job Not Available"));

    }

    public List<Job> getMyJob(String email){
        // Find the user (internally get the user/recruiter id)
        User recruiter = userRepository.findByEmail(email)
                .orElseThrow(()-> new UserNotFoundException("User not found"));

        return jobRepository.findByRecruiter(recruiter);

    }

    public void deleteJob(Long id,String email) throws AccessDeniedException {
        Job job = jobRepository.findById(id)
                        .orElseThrow(()->new JobNotFoundException("Job not found"));

        if(!job.getRecruiter().getEmail().equals(email)){
            throw new AccessDeniedException("you are not allowed to delete this job");
        }
        jobRepository.deleteById(id);
    }

    public Job updateJob(Long id,Job updatedJob,String email){
        Job job = jobRepository.findById(id)
                .orElseThrow(()-> new JobNotFoundException("Job not found"));

        if(!job.getRecruiter().getEmail().equals(email)){
            throw new AccessDeniedException("you are not allowed to update this job");
        }

        job.setTitle(updatedJob.getTitle());
        job.setDescription(updatedJob.getDescription());
        job.setLocation(updatedJob.getLocation());
        job.setMinSalary(updatedJob.getMinSalary());
        job.setMaxSalary(updatedJob.getMaxSalary());
        job.setCompany(updatedJob.getCompany());
        job.setJobType(updatedJob.getJobType());
        job.setExperience(updatedJob.getExperience());
        job.setOpenings(updatedJob.getOpenings());

        job.setSkills(updatedJob.getSkills());
        job.setResponsibilities(updatedJob.getResponsibilities());
        job.setRequirements(updatedJob.getRequirements());

        return jobRepository.save(job);
    }

    public Page<Job> filterJobs(
            String keyword,
            String location,
            String jobType,
            String experience,
            Double minSalary,
            Double maxSalary,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return jobRepository.filterJobs(
                keyword,
                location,
                jobType,
                experience,
                minSalary,
                maxSalary,
                pageable
        );
    }

//    public Page<Job> searchJob(String keyword, Pageable pageable){
//        return jobRepository.findByTitleContainingIgnoreCase(keyword,pageable);
//    }
//
//    public Page<Job> searchByLocation(String location, Pageable pageable) {
//        return jobRepository.findByLocationContainingIgnoreCase(location, pageable);
//    }
//
//    public Page<Job> filterByJobType(String jobType, Pageable pageable) {
//        return jobRepository.findByJobType(jobType, pageable);
//    }
//
//    public Page<Job> filterByExperience(String experience, Pageable pageable) {
//        return jobRepository.findByExperience(experience, pageable);
//    }
}
