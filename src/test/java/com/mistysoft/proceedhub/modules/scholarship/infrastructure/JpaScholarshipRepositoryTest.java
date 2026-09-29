package com.mistysoft.proceedhub.modules.scholarship.infrastructure;

import com.mistysoft.proceedhub.modules.scholarship.domain.Scholarship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.Set;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static com.mistysoft.proceedhub.modules.scholarship.ScholarshipFixtures.sample;

class JpaScholarshipRepositoryTest {

    @Mock
    private ISpringDataScholarshipRepository repository;

    @InjectMocks
    private JpaScholarshipRepository jpaScholarshipRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSave() {
        Scholarship scholarship = sample("id");
        ScholarshipEntity scholarshipEntity = ScholarshipMapper.toEntity(scholarship);

        jpaScholarshipRepository.save(scholarship);

        ArgumentCaptor<ScholarshipEntity> scholarshipEntityCaptor = ArgumentCaptor.forClass(ScholarshipEntity.class);
        verify(repository).save(scholarshipEntityCaptor.capture());
        assertEquals(scholarshipEntity, scholarshipEntityCaptor.getValue());
    }

    @Test
    void testFindById() {
        String id = "id";
        ScholarshipEntity scholarshipEntity = ScholarshipMapper.toEntity(sample(id));
        when(repository.findById(id)).thenReturn(Optional.of(scholarshipEntity));

        Optional<Scholarship> result = jpaScholarshipRepository.findById(id);

        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId());
    }

    @Test
    void testFindAll() {
        ScholarshipEntity scholarshipEntity1 = ScholarshipMapper.toEntity(sample("id1"));
        ScholarshipEntity scholarshipEntity2 = ScholarshipMapper.toEntity(sample("id2"));

        when(repository.findAll()).thenReturn(List.of(scholarshipEntity1, scholarshipEntity2));

        List<Scholarship> result = jpaScholarshipRepository.findAll();

        assertEquals(2, result.size());
        assertEquals("id1", result.get(0).getId());
        assertEquals("id2", result.get(1).getId());
    }

    @Test
    void testDeleteById() {
        String id = "id";

        jpaScholarshipRepository.deleteById(id);

        verify(repository).deleteById(id);
    }
}
