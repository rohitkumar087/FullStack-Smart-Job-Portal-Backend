package com.jobportal.service;

import  com.jobportal.dto.ApplicationRequest;
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
    private EmailService emailService;

    @Autowired
    private CandidateRepository candidateRepository;

    public String applyJob(Long jobId , String email, ApplicationRequest request,MultipartFile resume) throws IOException {
        User candidate = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new JobNotFoundException("Job not found"));

        boolean alreadyApplied = applicationRepository.existsByCandidateAndJob(candidate, job);

        if(alreadyApplied){
            throw new AlreadyAppliedException("You have already applied for the job");
        }

        String resumeUrl = null;
        if(resume != null && !resume.isEmpty()){
            String filename = System.currentTimeMillis()+"_"+resume.getOriginalFilename();

            Path uploadDir = Paths.get("uploads");

            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            Path path = uploadDir.resolve(filename);

            Files.write(path,resume.getBytes());

            resumeUrl = path.toString();
        }

        Candidate profile = candidateRepository.findByUser(candidate)
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


        Application application = new Application();

        application.setCandidate(candidate);
        application.setJob(job);
        application.setAppliedAt(LocalDateTime.now());
        application.setResumeUrl(resumeUrl);
        application.setCoverLetter(request.getCoverLetter());
        application.setCurrentLocation(request.getCurrentLocation());
        application.setExperience(request.getExperience());
        application.setExpectedSalary(request.getExpectedSalary());
        application.setSkills(request.getSkills());
        application.setStatus(ApplicationStatus.PENDING);

        applicationRepository.save(application);

        String body =
                "Dear " + candidate.getName() + ",\n\n" +

                        "Thank you for applying for the position of "
                        + job.getTitle() + " at "
                        + job.getCompany() + ".\n\n" +

                        "We have successfully received your application "
                        + "and our recruitment team will review your "
                        + "profile shortly.\n\n" +

                        "If your qualifications match our requirements, "
                        + "we will contact you regarding the next steps "
                        + "in the hiring process.\n\n" +

                        "Best Regards,\n" +
                        job.getCompany() + " Recruitment Team";

        emailService.sendEmail(
                candidate.getEmail(),
                "Application Submitted Successfully ",
                body
        );

        return "Job Applied Successfully. Please check your mail for confirmation.";
    }

    public List<Application> getAppliedJob(String email){
        User candidate = userRepository.findByEmail(email)
                .orElseThrow((()-> new UserNotFoundException("User Not Found")));

        return applicationRepository.findByCandidate(candidate);
    }

    public List<Application> getApplicants(Long jobId,String email) throws AccessDeniedException {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(()-> new JobNotFoundException("Job Not Found!"));

        if(!job.getRecruiter().getEmail().equals(email)){
            throw new AccessDeniedException("You are not allowed to view applicants");
        }

        return applicationRepository.findByJob(job);
    }

    public String updateApplicationStatus(Long applicationId,String email, ApplicationStatus status) throws AccessDeniedException {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(()-> new JobNotFoundException("Application not found"));

        if(!application.getJob().getRecruiter().getEmail().equals(email)){
            throw new AccessDeniedException("You are not allowed to update this application");
        }

        application.setStatus(status);
        applicationRepository.save(application);

        String subject = "";
        String body = "";

        switch (status){

            case SHORTLISTED:

                subject = "Congratulations! Your Application Has Been Accepted";

                body =
                        "Dear " + application.getCandidate().getName() + ",\n\n" +

                                "Congratulations!\n\n" +

                                "We are pleased to inform you that your application "
                                + "for the position of "
                                + application.getJob().getTitle()
                                + " at "
                                + application.getJob().getCompany()
                                + " has been successfully shortlisted.\n\n" +

                                "Our recruitment team will contact you shortly "
                                + "regarding the next steps in the hiring process.\n\n" +

                                "We appreciate your interest in joining "
                                + application.getJob().getCompany()
                                + " and look forward to connecting with you.\n\n" +

                                "Best Regards,\n" +
                                application.getJob().getCompany()
                                + " Recruitment Team";

                break;



            case REJECTED:

                subject = "Application Status Update";

                body =
                        "Dear " + application.getCandidate().getName() + ",\n\n" +

                                "Thank you for your interest in the position of "
                                + application.getJob().getTitle()
                                + " at "
                                + application.getJob().getCompany()
                                + ".\n\n" +

                                "After careful consideration, we regret to inform you "
                                + "that we will not be moving forward with your "
                                + "application at this time.\n\n" +

                                "We truly appreciate the time and effort you invested "
                                + "in the application process.\n\n" +

                                "We encourage you to apply for future opportunities "
                                + "that match your skills and experience.\n\n" +

                                "Best wishes for your future career.\n\n" +

                                "Best Regards,\n" +
                                application.getJob().getCompany()
                                + " Recruitment Team";

                break;


            default:

                subject = "Your Application is Under Review";

                body =
                        "Dear " + application.getCandidate().getName() + ",\n\n" +

                                "Thank you for applying for the position of "
                                + application.getJob().getTitle()
                                + " at "
                                + application.getJob().getCompany()
                                + ".\n\n" +

                                "We would like to inform you that your "
                                + "application is currently under review by "
                                + "our recruitment team.\n\n" +

                                "We appreciate your patience during the "
                                + "selection process.\n\n" +

                                "If your profile matches our requirements, "
                                + "we will contact you regarding the next "
                                + "steps.\n\n" +

                                "Best Regards,\n" +
                                application.getJob().getCompany()
                                + " Recruitment Team";
        }

        emailService.sendEmail(
                application.getCandidate().getEmail(),
                subject,
                body
        );

        return "Status updated. Check you mail !!";
    }

    public String uploadResume(Long applicationId, MultipartFile file, String email) throws IOException {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        if (!application.getCandidate().getEmail().equals(email)){
            throw new UserNotFoundException("User not found");
        }

        String filename = System.currentTimeMillis()+"_"+file.getOriginalFilename();

        Path path = Paths.get("uploads/"+filename);

        Files.write(path,file.getBytes());

        application.setResumeUrl(path.toString());

        applicationRepository.save(application);

        return "Resume uploaded successfully!!";
    }


    public ResponseEntity<Resource> downloadResume(Long applicationId) throws IOException {

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found!!"));

        String resumeUrl = application.getResumeUrl();

        if (resumeUrl == null || resumeUrl.isEmpty()) {
            throw new RuntimeException("Resume not uploaded");
        }

        Path path = Paths.get(resumeUrl).normalize();

        Resource resource = new UrlResource(path.toUri());

        if (!resource.exists()) {
            throw new RuntimeException("Resume not found");
        }

        String contentType = Files.probeContentType(path);

        if (contentType == null) {
            contentType = "application/pdf";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + resource.getFilename() + "\""
                )
                .body(resource);
    }

}
