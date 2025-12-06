package com.recruitment.system.service;

import com.recruitment.system.dto.request.LoginRequest;
import com.recruitment.system.dto.request.UserSignupRequest;
import com.recruitment.system.dto.response.AuthResponse;
import com.recruitment.system.model.Profile;
import com.recruitment.system.model.User;
import com.recruitment.system.model.UserType;
import com.recruitment.system.repository.UserRepository;
import com.recruitment.system.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Transactional
    public void signup(UserSignupRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email is already in use.");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setUserType(request.getUserType());
        user.setAddress(request.getAddress());
        user.setProfileHeadline(request.getProfileHeadline());

        // For applicants, create an empty profile
        if (request.getUserType() == UserType.APPLICANT) {
            Profile profile = new Profile();
            user.setProfile(profile);
        }

        userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        var jwtToken = jwtService.generateToken(user);
        return new AuthResponse(jwtToken);
    }
}