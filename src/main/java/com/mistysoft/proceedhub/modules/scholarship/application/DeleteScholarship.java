package com.mistysoft.proceedhub.modules.scholarship.application;

import com.mistysoft.proceedhub.modules.scholarship.domain.ScholarshipRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteScholarship {
    
    private final ScholarshipRepository scholarshipRepository;

    public DeleteScholarship(ScholarshipRepository scholarshipRepository) {
        this.scholarshipRepository = scholarshipRepository; 
    }

    @Transactional
    public void execute(String id) {
        if (scholarshipRepository.findById(id).isEmpty()) {
            throw new ScholarshipNotFoundException(id);
        }
        scholarshipRepository.deleteById(id); 
    }
}
