package com.recruitment.system.service;

import com.recruitment.system.dto.response.JobResponse;
import com.recruitment.system.dto.response.MyApplicationResponse;
import com.recruitment.system.exception.ResourceNotFoundException;
import com.recruitment.system.model.Job;
import com.recruitment.system.model.JobApplication;
import com.recruitment.system.model.User;
import com.recruitment.system.repository.JobApplicationRepository;
import com.recruitment.system.repository.JobRepository;
import com.recruitment.system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final ModelMapper modelMapper;

    public List<JobResponse> getAllJobs() {
        return jobRepository.findAll().stream()
                .map(job -> {
                    JobResponse response = modelMapper.map(job, JobResponse.class);
                    response.setPostedBy(job.getPostedBy().getName());
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void applyForJob(Long jobId, String applicantEmail) {
        User applicant = userRepository.findByEmail(applicantEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Applicant not found."));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with ID: " + jobId));

        if (applicant.getProfile() == null || !StringUtils.hasText(applicant.getProfile().getResumeFileAddress())) {
            throw new IllegalStateException("Please upload a resume before applying for a job.");
        }

        // Check if already applied
        jobApplicationRepository.findByApplicantIdAndJobId(applicant.getId(), jobId)
                .ifPresent(application -> {
                    throw new IllegalArgumentException("You have already applied for this job.");
                });

        JobApplication application = new JobApplication();
        application.setApplicant(applicant);
        application.setJob(job);
        application.setAppliedOn(LocalDateTime.now());

        jobApplicationRepository.save(application);

        // Update total applications count
        job.setTotalApplications(job.getTotalApplications() + 1);
        jobRepository.save(job);
    }

    public List<MyApplicationResponse> getMyApplications(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return jobApplicationRepository.findByApplicantId(user.getId()).stream()
                .map(app -> {
                    MyApplicationResponse res = new MyApplicationResponse();
                    res.setApplicationId(app.getId());
                    res.setJobTitle(app.getJob().getTitle());
                    res.setCompanyName(app.getJob().getCompanyName());
                    res.setStatus(app.getStatus());
                    res.setAppliedOn(app.getAppliedOn());
                    return res;
                })
                .collect(Collectors.toList());
    }
}