package com.jobportal.repository;

import com.jobportal.entity.Application;
import com.jobportal.entity.ApplicationStatus;
import com.jobportal.entity.Job;
import com.jobportal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application,Long> {

    boolean existsByCandidateAndJob(User candidate, Job job);
    List<Application> findByCandidate(User candidate);
    List<Application> findByJob(Job job);
    List<Application> findByStatus(ApplicationStatus status);
    List<Application> findByJobRecruiter(User recruiter);


}
