package com.mistysoft.proceedhub.modules.scholarship.application;

import com.mistysoft.proceedhub.modules.scholarship.application.dto.ScholarshipDTO;
import com.mistysoft.proceedhub.modules.scholarship.domain.*;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import java.util.Set;

import static com.mistysoft.proceedhub.modules.scholarship.ScholarshipFixtures.sample;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UpdateScholarshipTest {
    private final IScholarshipRepository repository = mock(IScholarshipRepository.class);
    private final UpdateScholarship update = new UpdateScholarship(repository);

    @Test
    void updatesRequestedFieldsAndKeepsTheOtherFields() {
        Scholarship original = sample("id");
        when(repository.findById("id")).thenReturn(Optional.of(original));
        Scholarship updated = update.execute(ScholarshipDTO.builder().title("New title")
                .requirements(Set.of()).build(), "id");

        assertEquals("New title", updated.getTitle());
        assertEquals(original.getDate(), updated.getDate());
        assertEquals(original.getDescription(), updated.getDescription());
        assertTrue(updated.getRequirements().isEmpty());
        assertEquals("id", updated.getId());
        verify(repository).save(updated);
    }

    @Test
    void rejectsBlankUpdatesWithoutSaving() {
        when(repository.findById("id")).thenReturn(Optional.of(sample("id")));
        assertThrows(IllegalArgumentException.class,
                () -> update.execute(ScholarshipDTO.builder().title(" ").build(), "id"));
        verify(repository, never()).save(any());
    }

    @Test
    void reportsMissingScholarshipsWithoutSaving() {
        when(repository.findById("missing")).thenReturn(Optional.empty());
        assertThrows(ScholarshipNotFoundException.class,
                () -> update.execute(ScholarshipDTO.builder().build(), "missing"));
        verify(repository, never()).save(any());
    }
}
