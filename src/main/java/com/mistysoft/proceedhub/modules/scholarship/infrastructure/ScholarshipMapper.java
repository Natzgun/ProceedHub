package com.mistysoft.proceedhub.modules.scholarship.infrastructure;

import com.mistysoft.proceedhub.modules.scholarship.domain.*;
import java.util.Set;
import java.util.stream.Collectors;

public class ScholarshipMapper {

    private ScholarshipMapper() {
        throw new UnsupportedOperationException("This is a utility class, cannot be instantiated");
    }
    
    public static ScholarshipEntity toEntity(Scholarship scholarship) {
        ScholarshipEntity entity = new ScholarshipEntity();
        entity.setId(scholarship.getId());
        entity.setTitle(scholarship.getTitle());
        entity.setDescription(scholarship.getDescription());
        entity.setDate(scholarship.getDate());
        entity.setImage(scholarship.getImage());
        entity.setCountry(scholarship.getCountry());
        entity.setContinent(scholarship.getContinent());
        entity.setMoreInfo(scholarship.getMoreInfo());
        entity.setRequirements(scholarship.getRequirements().stream().map(requirement -> {
            RequirementEmbeddable value = new RequirementEmbeddable();
            value.setName(requirement.name());
            return value;
        }).collect(Collectors.toSet()));
        return entity;
    }

    public static Scholarship toDomain(ScholarshipEntity entity) {
        return Scholarship.restore(entity.getId(), new ScholarshipChanges(
                entity.getTitle(), entity.getDescription(), entity.getDate(), entity.getImage(),
                entity.getCountry(), entity.getContinent(), entity.getMoreInfo(),
                entity.getRequirements() == null ? Set.of() : entity.getRequirements().stream()
                        .map(requirement -> new Requirement(requirement.getName())).collect(Collectors.toSet())));
    }

}
