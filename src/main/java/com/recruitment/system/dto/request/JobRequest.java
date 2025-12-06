package com.recruitment.system.dto.request;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class JobRequest {
    @NotBlank
    private String title;
    @NotBlank
    private String description;
    @NotBlank
    private String companyName;
}