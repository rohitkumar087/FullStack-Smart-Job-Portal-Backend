package com.jobportal.repository;

import com.jobportal.entity.Job;
import com.jobportal.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job,Long> {
        List<Job> findByRecruiter(User recruiter);

        @Query("""
        SELECT j FROM Job j
        WHERE
        (:keyword IS NULL OR :keyword = '' OR
            LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(j.company) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(j.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
        )
        AND (:location IS NULL OR :location = '' OR
            LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%'))
        )
        AND (:jobType IS NULL OR :jobType = '' OR
            j.jobType = :jobType
        )
        AND (:experience IS NULL OR :experience = '' OR
            j.experience = :experience
        )
        AND (:minSalary IS NULL OR
            j.maxSalary >= :minSalary
        )
        AND (:maxSalary IS NULL OR
            j.minSalary <= :maxSalary
        )
        """)
        Page<Job> filterJobs(
                @Param("keyword") String keyword,
                @Param("location") String location,
                @Param("jobType") String jobType,
                @Param("experience") String experience,
                @Param("minSalary") Double minSalary,
                @Param("maxSalary") Double maxSalary,
                Pageable pageable
        );
}
