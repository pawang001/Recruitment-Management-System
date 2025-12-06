package com.recruitment.system.dto.response;

import com.recruitment.system.model.UserType;
import lombok.Data;

@Data
public class ApplicantResponse {
    private Long id;
    private String name;
    private String email;
    private String address;
    private String profileHeadline;
    private UserType userType;
    private ProfileResponse profile;
}