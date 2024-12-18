package com.mistysoft.proceedhub.modules.scholarship.domain;

import lombok.Builder;
import lombok.Getter;
import java.util.Set;
import java.time.ZonedDateTime;

@Getter
@Builder
public class Scholarship {
    private final String id;
    private final String title;
    private final String description;
    private final ZonedDateTime date;
    private final String image;
    private final String country;
    private final String continent;
    private final String moreInfo;
    private final Set<Requirement> requirements;

}