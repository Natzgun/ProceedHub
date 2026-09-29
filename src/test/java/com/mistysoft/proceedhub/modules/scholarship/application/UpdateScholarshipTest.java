package com.mistysoft.proceedhub.modules.scholarship.application;

import com.mistysoft.proceedhub.modules.scholarship.domain.IScholarshipRepository;
import com.mistysoft.proceedhub.modules.scholarship.domain.Requirement;
import com.mistysoft.proceedhub.modules.scholarship.domain.Scholarship;
import com.mistysoft.proceedhub.modules.scholarship.application.dto.ScholarshipDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class UpdateScholarshipTest {

    @Mock
    private IScholarshipRepository scholarshipRepository;

    @InjectMocks
    private UpdateScholarship updateScholarship;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUpdateScholarshipFound() {
        String id = UUID.randomUUID().toString();
        Scholarship existingScholarship = Scholarship.builder()
                .id(id)
                .title("Old Title")
                .description("Old Description")
                .date(ZonedDateTime.now().minusDays(1))
                .image("Old Image")
                .country("Old Country")
                .continent("Old Continent")
                .moreInfo("Old More Info")
                .requirements(Set.of())
                .build();

        ScholarshipDTO request = ScholarshipDTO.builder()
                .title("New Title")
                .description("New Description")
                .date(ZonedDateTime.now())
                .image("New Image")
                .country("New Country")
                .continent("New Continent")
                .moreInfo("New More Info")
                .requirements(Set.of())
                .build();

        when(scholarshipRepository.findById(id)).thenReturn(Optional.of(existingScholarship));

        Scholarship result = updateScholarship.execute(request, id);

        assertEquals(request.getTitle(), result.getTitle());
        assertEquals(request.getDescription(), result.getDescription());
        assertEquals(request.getDate(), result.getDate());
        assertEquals(request.getImage(), result.getImage());
        assertEquals(request.getCountry(), result.getCountry());
        assertEquals(request.getContinent(), result.getContinent());
        assertEquals(request.getMoreInfo(), result.getMoreInfo());
        assertEquals(request.getRequirements(), result.getRequirements());
    }

    @Test
    void testUpdateScholarshipNotFound() {
        String id = UUID.randomUUID().toString();
        ScholarshipDTO request = ScholarshipDTO.builder().build();
        when(scholarshipRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> updateScholarship.execute(request, id));
    }

    @Test
    void testUpdateScholarshipWithNullTitle() {
        String id = UUID.randomUUID().toString();
        Scholarship existingScholarship = Scholarship.builder()
                .id(id)
                .title("Old Title")
                .build();

        ScholarshipDTO request = ScholarshipDTO.builder()
                .title(null)
                .build();

        when(scholarshipRepository.findById(id)).thenReturn(Optional.of(existingScholarship));

        Scholarship result = updateScholarship.execute(request, id);

        assertEquals("Old Title", result.getTitle());
    }

    @Test
    void testUpdateScholarshipWithBlankTitle() {
        String id = UUID.randomUUID().toString();
        Scholarship existingScholarship = Scholarship.builder()
                .id(id)
                .title("Old Title")
                .build();

        ScholarshipDTO request = ScholarshipDTO.builder()
                .title("")
                .build();

        when(scholarshipRepository.findById(id)).thenReturn(Optional.of(existingScholarship));

        Scholarship result = updateScholarship.execute(request, id);

        assertEquals("Old Title", result.getTitle());
    }

    @Test
    void testUpdateScholarshipWithNullDescription() {
        String id = UUID.randomUUID().toString();
        Scholarship existingScholarship = Scholarship.builder()
                .id(id)
                .description("Old Description")
                .build();

        ScholarshipDTO request = ScholarshipDTO.builder()
                .description(null)
                .build();

        when(scholarshipRepository.findById(id)).thenReturn(Optional.of(existingScholarship));

        Scholarship result = updateScholarship.execute(request, id);

        assertEquals("Old Description", result.getDescription());
    }

    @Test
    void testUpdateScholarshipWithBlankDescription() {
        String id = UUID.randomUUID().toString();
        Scholarship existingScholarship = Scholarship.builder()
                .id(id)
                .description("Old Description")
                .build();

        ScholarshipDTO request = ScholarshipDTO.builder()
                .description("")
                .build();

        when(scholarshipRepository.findById(id)).thenReturn(Optional.of(existingScholarship));

        Scholarship result = updateScholarship.execute(request, id);

        assertEquals("Old Description", result.getDescription());
    }

    @Test
    void testUpdateScholarshipWithNullDate() {
        String id = UUID.randomUUID().toString();
        Scholarship existingScholarship = Scholarship.builder()
                .id(id)
                .date(ZonedDateTime.now().minusDays(1))
                .build();

        ScholarshipDTO request = ScholarshipDTO.builder()
                .date(null)
                .build();

        when(scholarshipRepository.findById(id)).thenReturn(Optional.of(existingScholarship));

        Scholarship result = updateScholarship.execute(request, id);

        assertEquals(existingScholarship.getDate(), result.getDate());
    }

    @Test
    void testUpdateScholarshipWithNullImage() {
        String id = UUID.randomUUID().toString();
        Scholarship existingScholarship = Scholarship.builder()
                .id(id)
                .image("Old Image")
                .build();

        ScholarshipDTO request = ScholarshipDTO.builder()
                .image(null)
                .build();

        when(scholarshipRepository.findById(id)).thenReturn(Optional.of(existingScholarship));

        Scholarship result = updateScholarship.execute(request, id);

        assertEquals("Old Image", result.getImage());
    }

    @Test
    void testUpdateScholarshipWithBlankImage() {
        String id = UUID.randomUUID().toString();
        Scholarship existingScholarship = Scholarship.builder()
                .id(id)
                .image("Old Image")
                .build();

        ScholarshipDTO request = ScholarshipDTO.builder()
                .image("")
                .build();

        when(scholarshipRepository.findById(id)).thenReturn(Optional.of(existingScholarship));

        Scholarship result = updateScholarship.execute(request, id);

        assertEquals("Old Image", result.getImage());
    }

    @Test
    void testUpdateScholarshipWithNullCountry() {
        String id = UUID.randomUUID().toString();
        Scholarship existingScholarship = Scholarship.builder()
                .id(id)
                .country("Old Country")
                .build();

        ScholarshipDTO request = ScholarshipDTO.builder()
                .country(null)
                .build();

        when(scholarshipRepository.findById(id)).thenReturn(Optional.of(existingScholarship));

        Scholarship result = updateScholarship.execute(request, id);

        assertEquals("Old Country", result.getCountry());
    }

    @Test
    void testUpdateScholarshipWithBlankCountry() {
        String id = UUID.randomUUID().toString();
        Scholarship existingScholarship = Scholarship.builder()
                .id(id)
                .country("Old Country")
                .build();

        ScholarshipDTO request = ScholarshipDTO.builder()
                .country("")
                .build();

        when(scholarshipRepository.findById(id)).thenReturn(Optional.of(existingScholarship));

        Scholarship result = updateScholarship.execute(request, id);

        assertEquals("Old Country", result.getCountry());
    }

    @Test
    void testUpdateScholarshipWithNullContinent() {
        String id = UUID.randomUUID().toString();
        Scholarship existingScholarship = Scholarship.builder()
                .id(id)
                .continent("Old Continent")
                .build();

        ScholarshipDTO request = ScholarshipDTO.builder()
                .continent(null)
                .build();

        when(scholarshipRepository.findById(id)).thenReturn(Optional.of(existingScholarship));

        Scholarship result = updateScholarship.execute(request, id);

        assertEquals("Old Continent", result.getContinent());
    }

    @Test
    void testUpdateScholarshipWithBlankContinent() {
        String id = UUID.randomUUID().toString();
        Scholarship existingScholarship = Scholarship.builder()
                .id(id)
                .continent("Old Continent")
                .build();

        ScholarshipDTO request = ScholarshipDTO.builder()
                .continent("")
                .build();

        when(scholarshipRepository.findById(id)).thenReturn(Optional.of(existingScholarship));

        Scholarship result = updateScholarship.execute(request, id);

        assertEquals("Old Continent", result.getContinent());
    }

    @Test
    void testUpdateScholarshipWithNullMoreInfo() {
        String id = UUID.randomUUID().toString();
        Scholarship existingScholarship = Scholarship.builder()
                .id(id)
                .moreInfo("Old More Info")
                .build();

        ScholarshipDTO request = ScholarshipDTO.builder()
                .moreInfo(null)
                .build();

        when(scholarshipRepository.findById(id)).thenReturn(Optional.of(existingScholarship));

        Scholarship result = updateScholarship.execute(request, id);

        assertEquals("Old More Info", result.getMoreInfo());
    }

    @Test
    void testUpdateScholarshipWithBlankMoreInfo() {
        String id = UUID.randomUUID().toString();
        Scholarship existingScholarship = Scholarship.builder()
                .id(id)
                .moreInfo("Old More Info")
                .build();

        ScholarshipDTO request = ScholarshipDTO.builder()
                .moreInfo("")
                .build();

        when(scholarshipRepository.findById(id)).thenReturn(Optional.of(existingScholarship));

        Scholarship result = updateScholarship.execute(request, id);

        assertEquals("Old More Info", result.getMoreInfo());
    }

    @Test
    void testUpdateScholarshipWithNullRequirements() {
        String id = UUID.randomUUID().toString();
        Scholarship existingScholarship = Scholarship.builder()
                .id(id)
                .requirements(Set.of(new Requirement("Proof of enrollment")))
                .build();

        ScholarshipDTO request = ScholarshipDTO.builder()
                .requirements(null)
                .build();

        when(scholarshipRepository.findById(id)).thenReturn(Optional.of(existingScholarship));

        Scholarship result = updateScholarship.execute(request, id);

        assertEquals(existingScholarship.getRequirements(), result.getRequirements());
    }
}
