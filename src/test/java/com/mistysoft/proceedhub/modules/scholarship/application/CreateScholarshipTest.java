package com.mistysoft.proceedhub.modules.scholarship.application;

import com.mistysoft.proceedhub.modules.scholarship.domain.*;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CreateScholarshipTest {
    private final ScholarshipRepository repository = mock(ScholarshipRepository.class);
    private final Clock clock = Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC);
    private final CreateScholarship create = new CreateScholarship(repository, clock);

    @Test
    void createsCompleteScholarshipWithServerAssignedDateAndId() {
        ScholarshipChanges request = new ScholarshipChanges("Research grant", "For students",
                ZonedDateTime.parse("2020-01-01T00:00:00Z"), "https://example.com/image.png",
                "Peru", "South America", "https://example.com",
                Set.of(new Requirement("Proof of enrollment")));

        Scholarship result = create.execute(request);

        assertNotNull(result.getId());
        assertEquals(ZonedDateTime.now(clock), result.getDate());
        verify(repository).save(result);
    }

    @Test
    void invalidDataNeverReachesPersistence() {
        ScholarshipChanges request = new ScholarshipChanges(" ", null, null, null, null, null, null, null);
        assertThrows(IllegalArgumentException.class, () -> create.execute(request));
        verify(repository, never()).save(any());
    }
}
