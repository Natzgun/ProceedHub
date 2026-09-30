package com.mistysoft.proceedhub.modules.scholarship.domain;

public record Requirement(String name) {
    public Requirement {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Requirement name is required");
        }
        name = name.trim();
    }
}
