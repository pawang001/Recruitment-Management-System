package com.recruitment.system.dto.request;

import com.recruitment.system.model.ApplicationStatus;
import lombok.Data;

@Data
public class ApplicationStatusUpdate {
    private ApplicationStatus status;
}