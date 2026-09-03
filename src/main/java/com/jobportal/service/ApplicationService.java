package com.jobportal.service;

import com.jobportal.dto.ApplicationRequest;
import com.jobportal.entity.*;
import com.jobportal.exception.AlreadyAppliedException;
import com.jobportal.exception.JobNotFoundException;
import com.jobportal.exception.UserNotFoundException;
import com.jobportal.repository.ApplicationRepository;
import com.jobportal.repository.CandidateRepository;
import com.jobportal.repository.JobRepository;
import com.jobportal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ApplicationService {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private CandidateRepository candidateRepository;


    public String applyJob(
            Long jobId,
            String email,
            ApplicationRequest request,
            MultipartFile resume
    ) throws IOException {

        User candidate = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found")
                );

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() ->
                        new JobNotFoundException("Job not found")
                );


        boolean alreadyApplied =
                applicationRepository.existsByCandidateAndJob(
                        candidate,
                        job
                );

        if (alreadyApplied) {
            throw new AlreadyAppliedException(
                    "You have already applied for the job"
            );
        }


        String resumeUrl = null;

        if (resume != null && !resume.isEmpty()) {

            String filename =
                    System.currentTimeMillis()
                            + "_"
                            + resume.getOriginalFilename();

            Path uploadDir = Paths.get("uploads");

            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            Path path = uploadDir.resolve(filename);

            Files.write(
                    path,
                    resume.getBytes()
            );

            resumeUrl = path.toString();
        }


        // Save or update candidate profile

        Candidate profile =
                candidateRepository.findByUser(candidate)
                        .orElse(new Candidate());

        profile.setUser(candidate);
        profile.setPhone(request.getPhone());
        profile.setLocation(request.getCurrentLocation());
        profile.setExperience(request.getExperience());
        profile.setExpectedSalary(request.getExpectedSalary());
        profile.setResumeUrl(resumeUrl);
        profile.setPortfolioUrl(request.getPortfolioUrl());
        profile.setLinkedinUrl(request.getLinkedinUrl());
        profile.setGithubUrl(request.getGithubUrl());

        if (request.getSkills() != null) {
            profile.setSkills(request.getSkills());
        }

        candidateRepository.save(profile);


        // Create application

        Application application = new Application();

        application.setCandidate(candidate);
        application.setJob(job);
        application.setAppliedAt(LocalDateTime.now());
        application.setResumeUrl(resumeUrl);
        application.setCoverLetter(request.getCoverLetter());
        application.setCurrentLocation(
                request.getCurrentLocation()
        );
        application.setExperience(
                request.getExperience()
        );
        application.setExpectedSalary(
                request.getExpectedSalary()
        );
        application.setSkills(
                request.getSkills()
        );
        application.setStatus(
                ApplicationStatus.PENDING
        );


        applicationRepository.save(application);


        return "Job Applied Successfully.";
    }


    public List<Application> getAppliedJob(String email) {

        User candidate = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User Not Found"
                        )
                );

        return applicationRepository
                .findByCandidate(candidate);
    }


    public List<Application> getApplicants(
            Long jobId,
            String email
    ) throws AccessDeniedException {

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() ->
                        new JobNotFoundException(
                                "Job Not Found!"
                        )
                );


        if (!job.getRecruiter()
                .getEmail()
                .equals(email)) {

            throw new AccessDeniedException(
                    "You are not allowed to view applicants"
            );
        }


        return applicationRepository.findByJob(job);
    }


    public String updateApplicationStatus(
            Long applicationId,
            String email,
            ApplicationStatus status
    ) throws AccessDeniedException {

        Application application =
                applicationRepository.findById(applicationId)
                        .orElseThrow(() ->
                                new JobNotFoundException(
                                        "Application not found"
                                )
                        );


        if (!application.getJob()
                .getRecruiter()
                .getEmail()
                .equals(email)) {

            throw new AccessDeniedException(
                    "You are not allowed to update this application"
            );
        }


        application.setStatus(status);

        applicationRepository.save(application);


        return "Application status updated successfully.";
    }


    public String uploadResume(
            Long applicationId,
            MultipartFile file,
            String email
    ) throws IOException {

        Application application =
                applicationRepository.findById(applicationId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Application not found"
                                )
                        );


        if (!application.getCandidate()
                .getEmail()
                .equals(email)) {

            throw new UserNotFoundException(
                    "User not found"
            );
        }


        String filename =
                System.currentTimeMillis()
                        + "_"
                        + file.getOriginalFilename();


        Path path =
                Paths.get("uploads/" + filename);


        Files.write(
                path,
                file.getBytes()
        );


        application.setResumeUrl(
                path.toString()
        );


        applicationRepository.save(application);


        return "Resume uploaded successfully!!";
    }


    public ResponseEntity<Resource> downloadResume(
            Long applicationId
    ) throws IOException {

        Application application =
                applicationRepository.findById(applicationId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Application not found!!"
                                )
                        );


        String resumeUrl =
                application.getResumeUrl();


        if (resumeUrl == null || resumeUrl.isEmpty()) {

            throw new RuntimeException(
                    "Resume not uploaded"
            );
        }


        Path path =
                Paths.get(resumeUrl)
                        .normalize();


        Resource resource =
                new UrlResource(
                        path.toUri()
                );


        if (!resource.exists()) {

            throw new RuntimeException(
                    "Resume not found"
            );
        }


        String contentType =
                Files.probeContentType(path);


        if (contentType == null) {

            contentType = "application/pdf";
        }


        return ResponseEntity.ok()
                .contentType(
                        MediaType.parseMediaType(
                                contentType
                        )
                )
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\""
                                + resource.getFilename()
                                + "\""
                )
                .body(resource);
    }
}