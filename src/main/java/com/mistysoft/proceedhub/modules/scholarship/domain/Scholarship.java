package com.mistysoft.proceedhub.modules.scholarship.domain;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Set;

public final class Scholarship {
    private final String id;
    private final String title;
    private final String description;
    private final ZonedDateTime date;
    private final String image;
    private final String country;
    private final String continent;
    private final String moreInfo;
    private final Set<Requirement> requirements;

    private Scholarship(String id, ScholarshipChanges values) {
        this.id = requireText(id, "id");
        this.title = requireText(values.title(), "title");
        this.description = requireText(values.description(), "description");
        if (values.date() == null) {
            throw new IllegalArgumentException("date is required");
        }
        this.date = values.date();
        this.image = requireText(values.image(), "image");
        this.country = requireText(values.country(), "country");
        this.continent = requireText(values.continent(), "continent");
        this.moreInfo = requireText(values.moreInfo(), "moreInfo");
        if (values.requirements() == null) {
            throw new IllegalArgumentException("requirements are required");
        }
        this.requirements = Set.copyOf(values.requirements());
    }

    public static Scholarship create(String id, ScholarshipChanges values) {
        return new Scholarship(id, Objects.requireNonNull(values, "values are required"));
    }

    public static Scholarship restore(String id, ScholarshipChanges values) {
        return create(id, values);
    }

    public Scholarship update(ScholarshipChanges changes) {
        Objects.requireNonNull(changes, "changes are required");
        return new Scholarship(id, new ScholarshipChanges(
                changes.title() == null ? title : changes.title(),
                changes.description() == null ? description : changes.description(),
                changes.date() == null ? date : changes.date(),
                changes.image() == null ? image : changes.image(),
                changes.country() == null ? country : changes.country(),
                changes.continent() == null ? continent : changes.continent(),
                changes.moreInfo() == null ? moreInfo : changes.moreInfo(),
                changes.requirements() == null ? requirements : changes.requirements()));
    }

    private static String requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " is required");
        }
        return value.trim();
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public ZonedDateTime getDate() { return date; }
    public String getImage() { return image; }
    public String getCountry() { return country; }
    public String getContinent() { return continent; }
    public String getMoreInfo() { return moreInfo; }
    public Set<Requirement> getRequirements() { return requirements; }
}
