package com.jobportal.dto;

import com.jobportal.entity.Role;
import lombok.Data;

@Data
public class RegisterRequest {
    private String name;
    private String email;
    private String password;
    private Role role;
    private String companyName;
    private String location;
    private String website;
    private String description;
}
