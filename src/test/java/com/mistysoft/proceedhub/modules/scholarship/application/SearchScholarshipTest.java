package com.mistysoft.proceedhub.modules.scholarship.application;

import com.mistysoft.proceedhub.modules.scholarship.domain.IScholarshipRepository;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import static com.mistysoft.proceedhub.modules.scholarship.ScholarshipFixtures.sample;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SearchScholarshipTest {
    private final IScholarshipRepository repository = mock(IScholarshipRepository.class);
    private final SearchScholarship search = new SearchScholarship(repository);

    @Test
    void findsExistingScholarshipAndReportsMissingId() {
        var scholarship = sample("id");
        when(repository.findById("id")).thenReturn(Optional.of(scholarship));
        assertSame(scholarship, search.execute("id"));
        assertThrows(ScholarshipNotFoundException.class, () -> search.execute("missing"));
    }
}
