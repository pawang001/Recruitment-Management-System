package com.recruitment.system.dto.request;
import com.recruitment.system.model.UserType;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserSignupRequest {
    @NotBlank(message = "Name is mandatory")
    private String name;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password is mandatory")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    @NotNull(message = "UserType is mandatory")
    private UserType userType;
    private String address;
    private String profileHeadline;
}