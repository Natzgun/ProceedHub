package com.mistysoft.proceedhub.modules.scholarship.domain;

import java.util.Optional;
import java.util.List;

public interface ScholarshipRepository {
    void save(Scholarship scholarship);
    Optional<Scholarship> findById(String id);  
    List<Scholarship> findAll();
    void deleteById(String id);
}
