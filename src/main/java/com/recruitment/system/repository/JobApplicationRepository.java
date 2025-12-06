package com.recruitment.system.repository;

import com.recruitment.system.model.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    Optional<JobApplication> findByApplicantIdAndJobId(Long applicantId, Long jobId);
    List<JobApplication> findByApplicantId(Long applicantId); // New method
}