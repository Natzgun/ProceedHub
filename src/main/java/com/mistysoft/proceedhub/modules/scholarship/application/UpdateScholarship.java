package com.mistysoft.proceedhub.modules.scholarship.application;

import com.mistysoft.proceedhub.modules.scholarship.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateScholarship {
    private final ScholarshipRepository repository;

    public UpdateScholarship(ScholarshipRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Scholarship execute(ScholarshipChanges request, String id) {
        Scholarship existing = repository.findById(id)
                .orElseThrow(() -> new ScholarshipNotFoundException(id));
        Scholarship updated = existing.update(request);
        repository.save(updated);
        return updated;
    }
}
