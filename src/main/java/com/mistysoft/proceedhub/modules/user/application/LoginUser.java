package com.mistysoft.proceedhub.modules.user.application;

import com.mistysoft.proceedhub.modules.user.domain.User;
import com.mistysoft.proceedhub.modules.user.domain.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class LoginUser {
    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    public LoginUser(UserRepository userRepository, PasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }

    public User execute(String username, String rawPassword) {
        if (username == null || username.isBlank() || rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        User user = userRepository.findByUsername(username.trim())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordHasher.matches(rawPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        return user;
    }
}
