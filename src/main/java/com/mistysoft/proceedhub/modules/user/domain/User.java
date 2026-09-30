package com.mistysoft.proceedhub.modules.user.domain;

import java.util.Objects;
import java.util.Set;

public final class User {
    private final UserId id;
    private final String username;
    private final String email;
    private final String passwordHash;
    private final Set<Role> roles;

    private User(UserId id, String username, String email, String passwordHash, Set<Role> roles) {
        this.id = Objects.requireNonNull(id, "id is required");
        this.username = requireText(username, "username");
        this.email = requireText(email, "email");
        if (!email.contains("@")) {
            throw new IllegalArgumentException("Invalid email");
        }
        this.passwordHash = requireText(passwordHash, "passwordHash");
        this.roles = Set.copyOf(Objects.requireNonNull(roles, "roles are required"));
        if (this.roles.isEmpty()) {
            throw new IllegalArgumentException("A user must have at least one role");
        }
    }

    public static User register(UserId id, String username, String email, String passwordHash) {
        return new User(id, username, email, passwordHash, Set.of(Role.USER));
    }

    public static User restore(UserId id, String username, String email, String passwordHash, Set<Role> roles) {
        return new User(id, username, email, passwordHash, roles);
    }

    private static String requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " is required");
        }
        return value.trim();
    }

    public UserId getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public Set<Role> getRoles() { return roles; }
}
