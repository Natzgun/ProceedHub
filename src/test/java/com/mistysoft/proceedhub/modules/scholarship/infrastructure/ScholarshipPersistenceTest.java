package com.mistysoft.proceedhub.modules.scholarship.infrastructure;

import com.mistysoft.proceedhub.modules.scholarship.domain.Scholarship;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static com.mistysoft.proceedhub.modules.scholarship.ScholarshipFixtures.sample;

@SpringBootTest
@Transactional
class ScholarshipPersistenceTest {
    @Autowired JpaScholarshipRepository adapter;

    @Test
    void requirementsSurviveJpaRoundTripWithoutPersistingDomainObjects() {
        Scholarship scholarship = sample("scholarship-1");

        adapter.save(scholarship);
        Scholarship restored = adapter.findById(scholarship.getId()).orElseThrow();

        assertEquals(scholarship.getRequirements(), restored.getRequirements());
    }
}
