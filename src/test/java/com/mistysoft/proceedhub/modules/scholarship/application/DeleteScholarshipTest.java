package com.mistysoft.proceedhub.modules.scholarship.application;

import com.mistysoft.proceedhub.modules.scholarship.domain.ScholarshipRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.Optional;
import static com.mistysoft.proceedhub.modules.scholarship.ScholarshipFixtures.sample;

class DeleteScholarshipTest {

    @Mock
    private ScholarshipRepository scholarshipRepository;

    @InjectMocks
    private DeleteScholarship deleteScholarship;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testDeleteScholarship() {
        String id = "test-id";
        when(scholarshipRepository.findById(id)).thenReturn(Optional.of(sample(id)));

        deleteScholarship.execute(id);

        verify(scholarshipRepository).deleteById(id);
    }

    @Test
    void missingScholarshipIsNotDeleted() {
        assertThrows(ScholarshipNotFoundException.class, () -> deleteScholarship.execute("missing"));
        org.mockito.Mockito.verify(scholarshipRepository, org.mockito.Mockito.never()).deleteById("missing");
    }
}
