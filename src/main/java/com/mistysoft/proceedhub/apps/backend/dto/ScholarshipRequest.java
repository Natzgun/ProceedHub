package com.mistysoft.proceedhub.apps.backend.dto;

import com.mistysoft.proceedhub.modules.scholarship.domain.Requirement;
import com.mistysoft.proceedhub.modules.scholarship.domain.ScholarshipChanges;

import java.time.ZonedDateTime;
import java.util.Set;
import java.util.stream.Collectors;

public record ScholarshipRequest(
        String title, String description, ZonedDateTime date, String image,
        String country, String continent, String moreInfo, Set<RequirementRequest> requirements) {

    public ScholarshipChanges toChanges() {
        return new ScholarshipChanges(title, description, date, image, country, continent, moreInfo,
                requirements == null ? null : requirements.stream()
                        .map(requirement -> {
                            if (requirement == null) {
                                throw new IllegalArgumentException("Requirement is required");
                            }
                            return new Requirement(requirement.name());
                        })
                        .collect(Collectors.toUnmodifiableSet()));
    }

    public record RequirementRequest(String name) {}
}
