package com.mistysoft.proceedhub.modules.scholarship.domain;

import java.time.ZonedDateTime;
import java.util.Set;

public record ScholarshipChanges(
        String title, String description, ZonedDateTime date, String image,
        String country, String continent, String moreInfo, Set<Requirement> requirements) {
}
