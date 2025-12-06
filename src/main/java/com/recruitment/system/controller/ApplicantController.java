package com.recruitment.system.controller;

import com.recruitment.system.dto.response.ApiResponse;
import com.recruitment.system.dto.response.MyApplicationResponse;
import com.recruitment.system.service.JobService;
import com.recruitment.system.service.ResumeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/applicant")
@RequiredArgsConstructor
@Tag(name = "Applicant Actions", description = "APIs for applicant-specific actions")
@SecurityRequirement(name = "bearerAuth")
public class ApplicantController {

    private final ResumeService resumeService;
    private final JobService jobService;

    @PostMapping(value = "/uploadResume", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload and process a resume", description = "Allows an Applicant to upload their resume (PDF or DOCX).")
    public ResponseEntity<ApiResponse> uploadResume(@RequestParam("file") MultipartFile file, Authentication authentication) throws IOException {
        resumeService.uploadAndProcessResume(authentication.getName(), file);
        return ResponseEntity.ok(new ApiResponse("Resume uploaded and processed successfully.", HttpStatus.OK));
    }

    @GetMapping("/my-applications")
    @Operation(summary = "View My Applications", description = "See history of applied jobs and their status.")
    public ResponseEntity<List<MyApplicationResponse>> getMyApplications(Authentication authentication) {
        return ResponseEntity.ok(jobService.getMyApplications(authentication.getName()));
    }
}