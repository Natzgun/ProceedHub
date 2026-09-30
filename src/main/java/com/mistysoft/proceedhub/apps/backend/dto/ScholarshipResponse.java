package com.mistysoft.proceedhub.apps.backend.dto;

import com.mistysoft.proceedhub.modules.scholarship.domain.Scholarship;

import java.time.ZonedDateTime;
import java.util.Set;
import java.util.stream.Collectors;

public record ScholarshipResponse(
        String id, String title, String description, ZonedDateTime date, String image,
        String country, String continent, String moreInfo, Set<RequirementResponse> requirements) {

    public static ScholarshipResponse from(Scholarship scholarship) {
        return new ScholarshipResponse(scholarship.getId(), scholarship.getTitle(),
                scholarship.getDescription(), scholarship.getDate(), scholarship.getImage(),
                scholarship.getCountry(), scholarship.getContinent(), scholarship.getMoreInfo(),
                scholarship.getRequirements().stream().map(requirement -> new RequirementResponse(requirement.name()))
                        .collect(Collectors.toUnmodifiableSet()));
    }

    public record RequirementResponse(String name) {}
}
