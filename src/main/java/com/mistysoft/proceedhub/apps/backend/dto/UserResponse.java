package com.mistysoft.proceedhub.apps.backend.dto;

import com.mistysoft.proceedhub.modules.user.domain.Role;
import com.mistysoft.proceedhub.modules.user.domain.User;
import java.util.Set;

public record UserResponse(String id, String username, String email, Set<Role> roles) {
    public static UserResponse from(User user) {
        return new UserResponse(user.getId().value(), user.getUsername(), user.getEmail(), user.getRoles());
    }
}
