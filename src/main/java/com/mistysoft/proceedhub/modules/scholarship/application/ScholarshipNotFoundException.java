package com.mistysoft.proceedhub.modules.scholarship.application;

public class ScholarshipNotFoundException extends RuntimeException {
    public ScholarshipNotFoundException(String id) {
        super("Scholarship not found: " + id);
    }
}
