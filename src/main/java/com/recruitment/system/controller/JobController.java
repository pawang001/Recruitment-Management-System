package com.recruitment.system.controller;

import com.recruitment.system.dto.response.ApiResponse;
import com.recruitment.system.dto.response.JobResponse;
import com.recruitment.system.service.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
@Tag(name = "Job Management", description = "APIs for viewing and applying for jobs")
@SecurityRequirement(name = "bearerAuth")
public class JobController {

    @Autowired
    private JobService jobService;

    @GetMapping
    @Operation(summary = "Fetch all job openings", description = "Returns a list of all available job openings. Accessible to all authenticated users.")
    public ResponseEntity<List<JobResponse>> getAllJobs() {
        return ResponseEntity.ok(jobService.getAllJobs());
    }

    @PostMapping("/apply")
    @Operation(summary = "Apply for a job", description = "Allows an Applicant to apply for a specific job. Requires APPLICANT role.")
    public ResponseEntity<ApiResponse> applyForJob(@RequestParam("job_id") Long jobId, Authentication authentication) {
        jobService.applyForJob(jobId, authentication.getName());
        return ResponseEntity.ok(new ApiResponse("Successfully applied for the job.", HttpStatus.OK));
    }
}