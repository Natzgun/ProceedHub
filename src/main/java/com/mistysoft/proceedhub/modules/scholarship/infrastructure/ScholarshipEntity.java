package com.mistysoft.proceedhub.modules.scholarship.infrastructure;

import jakarta.persistence.*;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.Set;

@Entity
@Table(name = "scholarships")
@Data
public class ScholarshipEntity {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(nullable = false)
    private String title;
    
    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private ZonedDateTime date;

    @Column(nullable = false)
    private String image;

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private String continent;

    @Column(nullable = false)
    private String moreInfo;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "scholarship_requirements", joinColumns = @JoinColumn(name = "scholarship_id"))
    private Set<RequirementEmbeddable> requirements;

}
