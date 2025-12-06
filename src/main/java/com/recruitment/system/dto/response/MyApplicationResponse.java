package com.recruitment.system.dto.response;

import com.recruitment.system.model.ApplicationStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MyApplicationResponse {
    private Long applicationId;
    private String jobTitle;
    private String companyName;
    private ApplicationStatus status;
    private LocalDateTime appliedOn;
}