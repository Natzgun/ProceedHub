package com.mistysoft.proceedhub.apps.backend;

import com.mistysoft.proceedhub.apps.backend.dto.ScholarshipRequest;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import static org.junit.jupiter.api.Assertions.*;

class ScholarshipRequestJsonTest {
    @Test
    void parsesRequirementsAsHttpValuesBeforeCreatingDomainObjects() {
        ScholarshipRequest request = new JsonMapper().readValue("""
                {"title":"Scholarship","requirements":[{"name":"Proof of enrollment"}]}
                """, ScholarshipRequest.class);

        assertEquals("Scholarship", request.title());
        assertEquals("Proof of enrollment", request.toChanges().requirements().iterator().next().name());
    }
}
