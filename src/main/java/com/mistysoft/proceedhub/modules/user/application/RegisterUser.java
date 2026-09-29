package com.mistysoft.proceedhub.modules.user.application;

import com.mistysoft.proceedhub.modules.user.domain.*;

import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.Locale;

@Service
public class RegisterUser {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;


    public RegisterUser(UserRepository userRepository, PasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }

    public User execute(String username, String email, String rawPassword) {
        if (username == null || username.isBlank() || email == null || email.isBlank()
                || rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("Username, email and password are required");
        }
        username = username.trim();
        email = email.trim().toLowerCase(Locale.ROOT);
        if (!email.contains("@")) {
            throw new IllegalArgumentException("Invalid email");
        }
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("User with this username already exists");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("User with this email already exists");
        }

        UserId userId = new UserId(UUID.randomUUID().toString());
        String hashedPassword = passwordHasher.hash(rawPassword);

        User user = User.register(userId, username, email, hashedPassword);
        userRepository.save(user);
        return user;
    }
}
