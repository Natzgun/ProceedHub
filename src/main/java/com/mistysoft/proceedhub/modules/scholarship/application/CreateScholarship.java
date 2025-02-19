package com.mistysoft.proceedhub.modules.scholarship.application;

import com.mistysoft.proceedhub.modules.scholarship.application.dto.ScholarshipDTO;
import com.mistysoft.proceedhub.modules.scholarship.domain.*;

import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.UUID;

@Service
public class CreateScholarship {

    private final IScholarshipRepository scholarshipRepository;

    public CreateScholarship(IScholarshipRepository scholarshipRepository) {
        this.scholarshipRepository = scholarshipRepository;
    }

    public Scholarship execute(ScholarshipDTO request) {
        String id = UUID.randomUUID().toString();
        Scholarship scholarship = Scholarship.builder()
                .id(id)
                .title(request.getTitle())
                .description(request.getDescription())
                .date(ZonedDateTime.now())
                .image(request.getImage())
                .country(request.getCountry())
                .continent(request.getContinent())
                .moreInfo(request.getMoreInfo())
                .requirements(request.getRequirements())
                .build();
        scholarshipRepository.save(scholarship);
        return scholarship;
    }
}
