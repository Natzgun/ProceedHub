package com.mistysoft.proceedhub.modules.scholarship.application;

import com.mistysoft.proceedhub.modules.scholarship.application.dto.ScholarshipDTO;
import com.mistysoft.proceedhub.modules.scholarship.domain.IScholarshipRepository;
import com.mistysoft.proceedhub.modules.scholarship.domain.Scholarship;
import com.mistysoft.proceedhub.modules.scholarship.domain.Requirement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.ZonedDateTime;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

class CreateScholarshipTest {

    @Mock
    private IScholarshipRepository scholarshipRepository;

    @InjectMocks
    private CreateScholarship createScholarship;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateScholarship() {
        ScholarshipDTO request = ScholarshipDTO.builder()
                .id(UUID.randomUUID().toString())
                .title("Test Title")
                .description("Test Description")
                .date(ZonedDateTime.now())
                .image("Test Image")
                .country("Test Country")
                .continent("Test Continent")
                .moreInfo("Test More Info")
                .requirements(Set.of(new Requirement("Proof of enrollment")))
                .build();

        createScholarship.execute(request);

        ArgumentCaptor<Scholarship> scholarshipCaptor = ArgumentCaptor.forClass(Scholarship.class);
        verify(scholarshipRepository).save(scholarshipCaptor.capture());
        Scholarship savedScholarship = scholarshipCaptor.getValue();

        assertEquals(request.getTitle(), savedScholarship.getTitle());
        assertEquals(request.getDescription(), savedScholarship.getDescription());
        assertEquals(request.getImage(), savedScholarship.getImage());
        assertEquals(request.getCountry(), savedScholarship.getCountry());
        assertEquals(request.getContinent(), savedScholarship.getContinent());
        assertEquals(request.getMoreInfo(), savedScholarship.getMoreInfo());
        assertEquals(request.getRequirements(), savedScholarship.getRequirements());
    }
}
