package com.mistysoft.proceedhub.modules.scholarship.application.dto;

import com.mistysoft.proceedhub.modules.scholarship.domain.Requirement;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.ZonedDateTime;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScholarshipDTO {
    private String id;
    private String title;
    private String description;
    private ZonedDateTime date;
    private String image;
    private String country;
    private String continent;
    private String moreInfo;
    private Set<Requirement> requirements;
}
