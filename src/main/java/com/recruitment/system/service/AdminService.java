package com.recruitment.system.service;

import com.recruitment.system.dto.request.JobRequest;
import com.recruitment.system.dto.request.JobUpdateRequest;
import com.recruitment.system.dto.response.ApplicantResponse;
import com.recruitment.system.dto.response.JobResponse;
import com.recruitment.system.exception.ResourceNotFoundException;
import com.recruitment.system.model.*;
import com.recruitment.system.repository.JobApplicationRepository;
import com.recruitment.system.repository.JobRepository;
import com.recruitment.system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final JobApplicationRepository jobApplicationRepository;

    @Transactional
    public JobResponse createJob(JobRequest jobRequest, String adminEmail) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Admin user not found."));

        Job job = new Job();
        job.setTitle(jobRequest.getTitle());
        job.setDescription(jobRequest.getDescription());
        job.setCompanyName(jobRequest.getCompanyName());
        job.setPostedBy(admin);
        job.setPostedOn(LocalDateTime.now());
        job.setTotalApplications(0);

        Job savedJob = jobRepository.save(job);
        return modelMapper.map(savedJob, JobResponse.class);
    }

    public JobResponse getJobById(Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with ID: " + jobId));

        JobResponse jobResponse = modelMapper.map(job, JobResponse.class);
        jobResponse.setPostedBy(job.getPostedBy().getName());

        return jobResponse;
    }

    public List<ApplicantResponse> getAllApplicants() {
        return userRepository.findByUserType(UserType.APPLICANT)
                .stream()
                .map(user -> modelMapper.map(user, ApplicantResponse.class))
                .collect(Collectors.toList());
    }

    public ApplicantResponse getApplicantById(Long applicantId) {
        User user = userRepository.findById(applicantId)
                .filter(u -> u.getUserType() == UserType.APPLICANT)
                .orElseThrow(() -> new ResourceNotFoundException("Applicant not found with ID: " + applicantId));
        return modelMapper.map(user, ApplicantResponse.class);
    }

    @Transactional
    public JobResponse updateJob(Long jobId, JobUpdateRequest request) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with ID: " + jobId));

        if (request.getTitle() != null) job.setTitle(request.getTitle());
        if (request.getDescription() != null) job.setDescription(request.getDescription());
        if (request.getCompanyName() != null) job.setCompanyName(request.getCompanyName());

        Job updatedJob = jobRepository.save(job);
        return modelMapper.map(updatedJob, JobResponse.class);
    }

    @Transactional
    public void deleteJob(Long jobId) {
        if (!jobRepository.existsById(jobId)) {
            throw new ResourceNotFoundException("Job not found with ID: " + jobId);
        }
        jobRepository.deleteById(jobId);
    }

    @Transactional
    public void updateApplicationStatus(Long applicationId, ApplicationStatus newStatus) {
        JobApplication application = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        application.setStatus(newStatus);
        jobApplicationRepository.save(application);
    }
}