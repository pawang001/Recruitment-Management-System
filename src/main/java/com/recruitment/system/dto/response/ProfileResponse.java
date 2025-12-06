package com.recruitment.system.dto.response;
import lombok.Data;
@Data
public class ProfileResponse {
    private String resumeFileAddress;
    private String skills;
    private String education;
    private String experience;
    private String extractedName;
    private String extractedEmail;
    private String extractedPhone;
}