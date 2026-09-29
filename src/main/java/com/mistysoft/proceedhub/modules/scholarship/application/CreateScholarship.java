package com.mistysoft.proceedhub.modules.scholarship.application;

import com.mistysoft.proceedhub.modules.scholarship.application.dto.ScholarshipDTO;
import com.mistysoft.proceedhub.modules.scholarship.domain.*;

import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.time.Clock;
import java.util.UUID;

@Service
public class CreateScholarship {

    private final IScholarshipRepository scholarshipRepository;
    private final Clock clock;

    public CreateScholarship(IScholarshipRepository scholarshipRepository, Clock clock) {
        this.scholarshipRepository = scholarshipRepository;
        this.clock = clock;
    }

    public Scholarship execute(ScholarshipDTO request) {
        String id = UUID.randomUUID().toString();
        Scholarship scholarship = Scholarship.create(id, new ScholarshipChanges(
                request.getTitle(), request.getDescription(), ZonedDateTime.now(clock),
                request.getImage(), request.getCountry(), request.getContinent(),
                request.getMoreInfo(), request.getRequirements()));
        scholarshipRepository.save(scholarship);
        return scholarship;
    }
}
