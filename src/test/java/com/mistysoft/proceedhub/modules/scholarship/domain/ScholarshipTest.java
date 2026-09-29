package com.mistysoft.proceedhub.modules.scholarship.domain;

import org.junit.jupiter.api.Test;
import java.util.HashSet;
import java.util.Set;
import static com.mistysoft.proceedhub.modules.scholarship.ScholarshipFixtures.*;
import static org.junit.jupiter.api.Assertions.*;

class ScholarshipTest {
    @Test
    void validatesRequiredFieldsAndRequirementName() {
        assertThrows(IllegalArgumentException.class, () -> new Requirement(" "));
        assertThrows(IllegalArgumentException.class,
                () -> Scholarship.create("id", new ScholarshipChanges(null, "description", details().date(),
                        "image", "country", "continent", "url", Set.of())));
        var data = details();
        assertThrows(IllegalArgumentException.class,
                () -> Scholarship.create("id", new ScholarshipChanges(data.title(), data.description(), data.date(),
                        data.image(), data.country(), data.continent(), data.moreInfo(), null)));
    }

    @Test
    void ownsRequirementsDefensivelyAndUpdatesWithoutChangingOriginal() {
        Set<Requirement> mutable = new HashSet<>(details().requirements());
        ScholarshipChanges values = details();
        Scholarship original = Scholarship.create("id", new ScholarshipChanges(values.title(), values.description(),
                values.date(), values.image(), values.country(), values.continent(), values.moreInfo(), mutable));
        mutable.clear();
        assertEquals(1, original.getRequirements().size());
        assertThrows(UnsupportedOperationException.class,
                () -> original.getRequirements().add(new Requirement("Extra document")));

        Scholarship updated = original.update(new ScholarshipChanges("New title", null, null,
                null, null, null, null, Set.of()));
        assertEquals(values.title(), original.getTitle());
        assertEquals("New title", updated.getTitle());
        assertEquals(values.date(), updated.getDate());
        assertTrue(updated.getRequirements().isEmpty());
    }
}
