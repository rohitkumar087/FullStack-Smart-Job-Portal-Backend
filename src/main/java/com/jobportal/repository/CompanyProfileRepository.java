package com.jobportal.repository;

import com.jobportal.entity.CompanyProfile;
import com.jobportal.entity.RecruiterStatus;
import com.jobportal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CompanyProfileRepository extends JpaRepository<CompanyProfile,Long> {
    Optional<CompanyProfile> findByRecruiter(User user);
    List<CompanyProfile> findByRecruiterStatus(RecruiterStatus status);
}
