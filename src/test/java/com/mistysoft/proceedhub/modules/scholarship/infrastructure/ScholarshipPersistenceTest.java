package com.mistysoft.proceedhub.modules.scholarship.infrastructure;

import com.mistysoft.proceedhub.modules.scholarship.domain.Requirement;
import com.mistysoft.proceedhub.modules.scholarship.domain.Scholarship;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class ScholarshipPersistenceTest {
    @Autowired JpaScholarshipRepository adapter;

    @Test
    void requirementsSurviveJpaRoundTripWithoutPersistingDomainObjects() {
        Scholarship scholarship = Scholarship.builder()
                .id("scholarship-1").title("Research grant").description("Research funding")
                .date(ZonedDateTime.now()).image("https://example.com/image.png")
                .country("Peru").continent("South America").moreInfo("https://example.com")
                .requirements(Set.of(new Requirement("Proof of enrollment"))).build();

        adapter.save(scholarship);
        Scholarship restored = adapter.findById(scholarship.getId()).orElseThrow();

        assertEquals(scholarship.getRequirements(), restored.getRequirements());
    }
}
