package com.mistysoft.proceedhub.modules.scholarship.application;

import com.mistysoft.proceedhub.modules.scholarship.application.dto.ScholarshipDTO;
import com.mistysoft.proceedhub.modules.scholarship.domain.*;
import org.springframework.stereotype.Service;

@Service
public class UpdateScholarship {
    private final IScholarshipRepository repository;

    public UpdateScholarship(IScholarshipRepository repository) {
        this.repository = repository;
    }

    public Scholarship execute(ScholarshipDTO request, String id) {
        Scholarship existing = repository.findById(id)
                .orElseThrow(() -> new ScholarshipNotFoundException(id));
        Scholarship updated = existing.update(new ScholarshipChanges(
                request.getTitle(), request.getDescription(), request.getDate(), request.getImage(),
                request.getCountry(), request.getContinent(), request.getMoreInfo(), request.getRequirements()));
        repository.save(updated);
        return updated;
    }
}
