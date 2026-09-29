package com.mistysoft.proceedhub.modules.user.domain;

public record UserId(String value) {
    public UserId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("User id is required");
        }
    }
}
