package com.mistysoft.proceedhub.modules.scholarship;

import com.mistysoft.proceedhub.modules.scholarship.domain.*;
import java.time.ZonedDateTime;
import java.util.Set;

public final class ScholarshipFixtures {
    private ScholarshipFixtures() {}

    public static ScholarshipChanges details() {
        return new ScholarshipChanges("Research grant", "Funding for students",
                ZonedDateTime.parse("2026-01-01T00:00:00Z"), "https://example.com/image.png",
                "Peru", "South America", "https://example.com/details",
                Set.of(new Requirement("Proof of enrollment")));
    }

    public static Scholarship sample(String id) {
        return Scholarship.create(id, details());
    }
}
