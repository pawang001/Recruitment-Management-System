package com.recruitment.system.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recruitment.system.dto.parser.ResumeParserResponse;
import com.recruitment.system.exception.ResourceNotFoundException;
import com.recruitment.system.model.Profile;
import com.recruitment.system.model.User;
import com.recruitment.system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResumeService {

    private final UserRepository userRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private static final Logger log = LoggerFactory.getLogger(ResumeService.class);

    @Value("${resume.parser.api.key}")
    private String apiKey;

    @Value("${resume.parser.api.url}")
    private String apiUrl;

    @Transactional
    public void uploadAndProcessResume(String userEmail, MultipartFile file) throws IOException {
        validateFile(file);

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));
        Profile profile = user.getProfile();
        if (profile == null) {
            throw new IllegalStateException("User does not have a profile. Only applicants can upload resumes.");
        }

        ResumeParserResponse response = callResumeParserApi(file);
        updateProfileFromApiResponse(profile, response);

        profile.setResumeFileAddress("uploads/" + user.getId() + "/" + StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename())));
        userRepository.save(user);
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty.");
        }
        String contentType = file.getContentType();
        if (contentType == null || (!contentType.equals("application/pdf") && !contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))) {
            throw new IllegalArgumentException("Invalid file type. Only PDF and DOCX are allowed.");
        }
    }

    private ResumeParserResponse callResumeParserApi(MultipartFile file) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.set("apikey", apiKey);

        ByteArrayResource contentsAsResource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        };

        HttpEntity<ByteArrayResource> requestEntity = new HttpEntity<>(contentsAsResource, headers);

        try {
            // Step 1: Execute the call and get the response as a raw String
            ResponseEntity<String> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity, String.class);
            String responseBody = responseEntity.getBody();

            // Step 2: Log the raw response for debugging
            log.info("Raw response from Resume Parser API: {}", responseBody);

            if (responseBody == null || responseBody.isEmpty()) {
                throw new RuntimeException("API returned an empty response body.");
            }

            // Step 3: Manually parse the string into your DTO
            return objectMapper.readValue(responseBody, ResumeParserResponse.class);

        } catch (HttpClientErrorException e) {
            String errorMessage = e.getResponseBodyAsString();
            throw new RuntimeException("Error from Resume Parser API: " + e.getStatusCode() + " - " + errorMessage);
        } catch (RestClientException e) {
            throw new RuntimeException("Failed to connect to the Resume Parser API: " + e.getMessage());
        } catch (IOException e) {
            // This catches JSON parsing errors
            throw new RuntimeException("Failed to parse the API response JSON: " + e.getMessage());
        }
    }

    private void updateProfileFromApiResponse(Profile profile, ResumeParserResponse response) {
        if (response.getName() != null) profile.setExtractedName(response.getName());
        if (response.getEmail() != null) profile.setExtractedEmail(response.getEmail());
        if (response.getPhone() != null) profile.setExtractedPhone(response.getPhone());

        if (response.getSkills() != null && !response.getSkills().isEmpty()) {
            profile.setSkills(String.join(", ", response.getSkills()));
        }

        if (response.getEducation() != null && !response.getEducation().isEmpty()) {
            String educationStr = response.getEducation().stream()
                    .map(edu -> edu.get("name"))
                    .filter(Objects::nonNull)
                    .map(Object::toString) // <-- Add this line
                    .collect(Collectors.joining("; "));
            profile.setEducation(educationStr);
        }

        if (response.getExperience() != null && !response.getExperience().isEmpty()) {
            String experienceStr = response.getExperience().stream()
                    .map(exp -> exp.get("name"))
                    .filter(Objects::nonNull)
                    .map(Object::toString)
                    .collect(Collectors.joining("; "));
            profile.setExperience(experienceStr);
        }
    }
}