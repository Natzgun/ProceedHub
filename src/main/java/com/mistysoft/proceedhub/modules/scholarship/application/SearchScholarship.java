package com.mistysoft.proceedhub.modules.scholarship.application;


import com.mistysoft.proceedhub.modules.scholarship.domain.*;
import org.springframework.stereotype.Service;

@Service
public class SearchScholarship {

    private final IScholarshipRepository scholarshipRepository;
    public SearchScholarship(IScholarshipRepository scholarshipRepository) {
        this.scholarshipRepository = scholarshipRepository;
    }

    public Scholarship execute(String id) {
        return scholarshipRepository.findById(id)
                .orElseThrow(() -> new ScholarshipNotFoundException(id));
    }
}
