package com.mistysoft.proceedhub.modules.scholarship.infrastructure;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
public class RequirementEmbeddable {
    private String name;
}
