package com.recruitment.system.controller;

import com.recruitment.system.dto.request.ApplicationStatusUpdate;
import com.recruitment.system.dto.request.JobRequest;
import com.recruitment.system.dto.request.JobUpdateRequest;
import com.recruitment.system.dto.response.ApiResponse;
import com.recruitment.system.dto.response.ApplicantResponse;
import com.recruitment.system.dto.response.JobResponse;
import com.recruitment.system.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin Management", description = "APIs for administrative tasks")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/job")
    @Operation(summary = "Create a job opening", description = "Allows an Admin to create a new job opening. Requires ADMIN role.")
    public ResponseEntity<JobResponse> createJob(@Valid @RequestBody JobRequest jobRequest, Authentication authentication) {
        JobResponse createdJob = adminService.createJob(jobRequest, authentication.getName());
        return new ResponseEntity<>(createdJob, HttpStatus.CREATED);
    }

    @GetMapping("/job/{job_id}")
    @Operation(summary = "Get job opening details", description = "Fetches details of a specific job opening. Requires ADMIN role.")
    public ResponseEntity<JobResponse> getJobById(@PathVariable("job_id") Long jobId) {
        return ResponseEntity.ok(adminService.getJobById(jobId));
    }

    @GetMapping("/applicants")
    @Operation(summary = "Get all applicants", description = "Fetches a list of all users with the APPLICANT role. Requires ADMIN role.")
    public ResponseEntity<List<ApplicantResponse>> getAllApplicants() {
        return ResponseEntity.ok(adminService.getAllApplicants());
    }

    @GetMapping("/applicant/{applicant_id}")
    @Operation(summary = "Get applicant details", description = "Fetches the profile and extracted resume data of a specific applicant. Requires ADMIN role.")
    public ResponseEntity<ApplicantResponse> getApplicantById(@PathVariable("applicant_id") Long applicantId) {
        return ResponseEntity.ok(adminService.getApplicantById(applicantId));
    }

    @PutMapping("/job/{job_id}")
    @Operation(summary = "Update a job", description = "Update details of an existing job.")
    public ResponseEntity<JobResponse> updateJob(@PathVariable("job_id") Long jobId, @RequestBody JobUpdateRequest request) {
        return ResponseEntity.ok(adminService.updateJob(jobId, request));
    }

    @DeleteMapping("/job/{job_id}")
    @Operation(summary = "Delete a job", description = "Remove a job opening.")
    public ResponseEntity<ApiResponse> deleteJob(@PathVariable("job_id") Long jobId) {
        adminService.deleteJob(jobId);
        return ResponseEntity.ok(new ApiResponse("Job deleted successfully", HttpStatus.OK));
    }

    @PutMapping("/application/{application_id}/status")
    @Operation(summary = "Update Application Status", description = "Change status (e.g., SHORTLISTED, REJECTED).")
    public ResponseEntity<ApiResponse> updateApplicationStatus(
            @PathVariable("application_id") Long applicationId,
            @RequestBody ApplicationStatusUpdate statusUpdate) {

        adminService.updateApplicationStatus(applicationId, statusUpdate.getStatus());
        return ResponseEntity.ok(new ApiResponse("Application status updated to " + statusUpdate.getStatus(), HttpStatus.OK));
    }
}