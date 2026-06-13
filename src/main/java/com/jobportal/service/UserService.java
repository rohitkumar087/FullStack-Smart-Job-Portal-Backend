package com.jobportal.service;

import com.jobportal.dto.LoginRequest;
import com.jobportal.dto.LoginResponse;
import com.jobportal.dto.RegisterRequest;
import com.jobportal.entity.CompanyProfile;
import com.jobportal.entity.RecruiterStatus;
import com.jobportal.entity.Role;
import com.jobportal.entity.User;
import com.jobportal.exception.UserNotFoundException;
import com.jobportal.repository.CompanyProfileRepository;
import com.jobportal.repository.UserRepository;
import com.jobportal.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CompanyProfileRepository companyProfileRepository;

    public User register(RegisterRequest request){
        if (request.getRole() == Role.ADMIN) {
            throw new RuntimeException("Admin registration is not allowed");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        User savedUser = userRepository.save(user);

        if(request.getRole() == Role.RECRUITER){
            CompanyProfile companyProfile = new CompanyProfile();

            companyProfile.setCompanyName(request.getCompanyName());
            companyProfile.setWebsite((request.getWebsite()));
            companyProfile.setLocation(request.getLocation());
            companyProfile.setDescription(request.getDescription());

            companyProfile.setRecruiterStatus(RecruiterStatus.PENDING);
            companyProfile.setRecruiter(savedUser);

            companyProfileRepository.save(companyProfile);
        }
    return savedUser;
    }

    public LoginResponse login(LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        if (authentication.isAuthenticated()) {

            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new UserNotFoundException("User not found"));

            String token = jwtUtil.generateToken(user.getEmail());

            return new LoginResponse(
                    token,
                    user.getName(),
                    user.getEmail(),
                    user.getRole().name()
            );
        }

        throw new BadCredentialsException("Invalid credentials");
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }
}
