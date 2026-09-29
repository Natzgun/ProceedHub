package com.mistysoft.proceedhub.modules.scholarship.application;

import com.mistysoft.proceedhub.modules.scholarship.domain.IScholarshipRepository;
import com.mistysoft.proceedhub.modules.scholarship.domain.Scholarship;
import com.mistysoft.proceedhub.modules.scholarship.domain.Requirement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class GetAllScholarshipsTest {

    @Mock
    private IScholarshipRepository scholarshipRepository;

    @InjectMocks
    private GetAllScholarships getAllScholarships;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllScholarships() {
        Scholarship scholarship1 = Scholarship.builder()
                .id(UUID.randomUUID().toString())
                .title("Title 1")
                .description("Description 1")
                .date(ZonedDateTime.now())
                .image("Image 1")
                .country("Country 1")
                .continent("Continent 1")
                .moreInfo("More Info 1")
                .requirements(Set.of(new Requirement("Proof of enrollment")))
                .build();

        Scholarship scholarship2 = Scholarship.builder()
                .id(UUID.randomUUID().toString())
                .title("Title 2")
                .description("Description 2")
                .date(ZonedDateTime.now())
                .image("Image 2")
                .country("Country 2")
                .continent("Continent 2")
                .moreInfo("More Info 2")
                .requirements(Set.of(new Requirement("Proof of enrollment")))
                .build();

        List<Scholarship> scholarships = List.of(scholarship1, scholarship2);
        when(scholarshipRepository.findAll()).thenReturn(scholarships);

        List<Scholarship> result = getAllScholarships.execute();

        assertEquals(scholarships, result);
    }
}
