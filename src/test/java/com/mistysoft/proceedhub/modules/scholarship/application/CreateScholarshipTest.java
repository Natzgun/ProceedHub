package com.mistysoft.proceedhub.modules.scholarship.application;

import com.mistysoft.proceedhub.modules.scholarship.application.dto.ScholarshipDTO;
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
    private final IScholarshipRepository repository = mock(IScholarshipRepository.class);
    private final Clock clock = Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC);
    private final CreateScholarship create = new CreateScholarship(repository, clock);

    @Test
    void createsCompleteScholarshipWithServerAssignedDateAndId() {
        ScholarshipDTO request = ScholarshipDTO.builder().id("client-id")
                .title("Research grant").description("For students")
                .date(ZonedDateTime.parse("2020-01-01T00:00:00Z"))
                .image("https://example.com/image.png").country("Peru")
                .continent("South America").moreInfo("https://example.com")
                .requirements(Set.of(new Requirement("Proof of enrollment"))).build();

        Scholarship result = create.execute(request);

        assertNotEquals("client-id", result.getId());
        assertEquals(ZonedDateTime.now(clock), result.getDate());
        verify(repository).save(result);
    }

    @Test
    void invalidDataNeverReachesPersistence() {
        ScholarshipDTO request = ScholarshipDTO.builder().title(" ").build();
        assertThrows(IllegalArgumentException.class, () -> create.execute(request));
        verify(repository, never()).save(any());
    }
}
