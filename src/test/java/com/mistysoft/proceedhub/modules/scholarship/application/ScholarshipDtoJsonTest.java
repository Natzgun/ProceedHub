package com.mistysoft.proceedhub.modules.scholarship.application;

import com.mistysoft.proceedhub.modules.scholarship.application.dto.ScholarshipDTO;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import static org.junit.jupiter.api.Assertions.*;

class ScholarshipDtoJsonTest {
    @Test
    void acceptsCreationPayloadOnJacksonThree() {
        ScholarshipDTO request = new JsonMapper().readValue("""
                {"title":"Scholarship","requirements":[]}
                """, ScholarshipDTO.class);

        assertEquals("Scholarship", request.getTitle());
        assertTrue(request.getRequirements().isEmpty());
    }
}
