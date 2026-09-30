package com.mistysoft.proceedhub.modules.scholarship.application;

import com.mistysoft.proceedhub.modules.scholarship.domain.ScholarshipRepository;
import org.junit.jupiter.api.Test;
import java.util.List;
import static com.mistysoft.proceedhub.modules.scholarship.ScholarshipFixtures.sample;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class GetAllScholarshipsTest {
    @Test
    void returnsRepositoryResults() {
        ScholarshipRepository repository = mock(ScholarshipRepository.class);
        var scholarships = List.of(sample("first"), sample("second"));
        when(repository.findAll()).thenReturn(scholarships);
        assertEquals(scholarships, new GetAllScholarships(repository).execute());
    }
}
