package com.mistysoft.proceedhub.apps.backend;

import com.mistysoft.proceedhub.modules.scholarship.application.*;
import com.mistysoft.proceedhub.modules.scholarship.application.dto.ScholarshipDTO;
import com.mistysoft.proceedhub.modules.scholarship.domain.Scholarship;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static com.mistysoft.proceedhub.modules.scholarship.ScholarshipFixtures.sample;

class ScholarshipControllerTest {

    @Mock
    private CreateScholarship createScholarship;

    @Mock
    private UpdateScholarship updateScholarship;

    @Mock
    private GetAllScholarships getAllScholarships;

    @Mock
    private SearchScholarship searchScholarship;

    @Mock
    private DeleteScholarship deleteScholarship;

    @InjectMocks
    private ScholarshipController scholarshipController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateScholarship() {
        ScholarshipDTO scholarshipDTO = ScholarshipDTO.builder().build();
        Scholarship scholarship = sample("created");

        when(createScholarship.execute(scholarshipDTO)).thenReturn(scholarship);

        ResponseEntity<String> response = scholarshipController.createScholarship(scholarshipDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Scholarship created successfully", response.getBody());
        verify(createScholarship, times(1)).execute(scholarshipDTO);
    }

    @Test
    void testGetScholarshipById() {
        String id = UUID.randomUUID().toString();
        Scholarship scholarship = sample(id);
        when(searchScholarship.execute(id)).thenReturn(scholarship);

        ResponseEntity<Scholarship> response = scholarshipController.getScholarshipById(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(scholarship, response.getBody());
        verify(searchScholarship, times(1)).execute(id);
    }

    @Test
    void testGetAllScholarships() {
        Scholarship scholarship1 = sample("first");
        Scholarship scholarship2 = sample("second");
        List<Scholarship> scholarships = List.of(scholarship1, scholarship2);
        when(getAllScholarships.execute()).thenReturn(scholarships);

        ResponseEntity<List<Scholarship>> response = scholarshipController.getAllScholarships();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(scholarships, response.getBody());
        verify(getAllScholarships, times(1)).execute();
    }

    @Test
    void testUpdateScholarship() {
        String id = UUID.randomUUID().toString();
        ScholarshipDTO scholarshipDTO = ScholarshipDTO.builder().build();
        Scholarship scholarship = sample(id);

        when(updateScholarship.execute(scholarshipDTO, id)).thenReturn(scholarship);

        ResponseEntity<String> response = scholarshipController.updateScholarship(scholarshipDTO, id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Scholarship updated successfully", response.getBody());
        verify(updateScholarship, times(1)).execute(scholarshipDTO, id);
    }

    @Test
    void testDeleteScholarship() {
        String id = UUID.randomUUID().toString();
        doNothing().when(deleteScholarship).execute(id);

        ResponseEntity<String> response = scholarshipController.deleteScholarship(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Scholarship deleted successfully", response.getBody());
        verify(deleteScholarship, times(1)).execute(id);
    }
}
