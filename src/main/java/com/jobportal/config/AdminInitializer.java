package com.jobportal.config;

import com.jobportal.entity.Role;
import com.jobportal.entity.User;
import com.jobportal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements CommandLineRunner {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.admin.name}")
    private String name;
    @Value("${app.admin.email}")
    private String email;
    @Value("${app.admin.password}")
    private String password;

    @Override
    public void run(String... args) throws Exception {
        boolean adminExist = userRepository.findByEmail(email).isPresent();

        if(!adminExist){
            User admin = new User();

            admin.setName(name);
            admin.setEmail(email);
            admin.setPassword(passwordEncoder.encode(password));
            admin.setRole(Role.ADMIN);

            userRepository.save(admin);

            System.out.println("Admin created successfully");
        }
        else{
            System.out.println("Admin already exist");
        }
    }
}
