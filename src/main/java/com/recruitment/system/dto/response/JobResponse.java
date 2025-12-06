package com.recruitment.system.dto.response;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class JobResponse {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime postedOn;
    private int totalApplications;
    private String companyName;
    private String postedBy;
}