package com.mistysoft.proceedhub.modules.scholarship.application;

import com.mistysoft.proceedhub.modules.scholarship.domain.*;

import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.time.Clock;
import java.util.UUID;

@Service
public class CreateScholarship {

    private final ScholarshipRepository scholarshipRepository;
    private final Clock clock;

    public CreateScholarship(ScholarshipRepository scholarshipRepository, Clock clock) {
        this.scholarshipRepository = scholarshipRepository;
        this.clock = clock;
    }

    public Scholarship execute(ScholarshipChanges request) {
        String id = UUID.randomUUID().toString();
        Scholarship scholarship = Scholarship.create(id, new ScholarshipChanges(
                request.title(), request.description(), ZonedDateTime.now(clock),
                request.image(), request.country(), request.continent(),
                request.moreInfo(), request.requirements()));
        scholarshipRepository.save(scholarship);
        return scholarship;
    }
}
