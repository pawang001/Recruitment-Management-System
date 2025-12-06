package com.recruitment.system.dto.request;

import lombok.Data;

@Data
public class JobUpdateRequest {
    private String title;
    private String description;
    private String companyName;
}