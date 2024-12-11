package com.mistysoft.proceedhub.apps.backend;

import com.mistysoft.proceedhub.modules.scholarship.application.*;
import com.mistysoft.proceedhub.modules.scholarship.application.dto.ScholarshipDTO;
import com.mistysoft.proceedhub.modules.scholarship.domain.Scholarship;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scholarships")
public class ScholarshipController {

    private final CreateScholarship createScholarship;
    private final UpdateScholarship updateScholarship;
    private final GetAllScholarships getAllScholarships;
    private final SearchScholarship searchScholarship;
    private final DeleteScholarship deleteScholarship; 

    public ScholarshipController(CreateScholarship createScholarship, UpdateScholarship updateScholarship, GetAllScholarships getAllScholarships, SearchScholarship searchScholarship, DeleteScholarship deleteScholarship) {
        this.createScholarship = createScholarship;
        this.updateScholarship = updateScholarship;
        this.getAllScholarships = getAllScholarships;
        this.searchScholarship = searchScholarship;
        this.deleteScholarship = deleteScholarship; 
    }

    @PostMapping("/create")
    public ResponseEntity<String> createScholarship(@RequestBody ScholarshipDTO scholarship) {
        createScholarship.execute(scholarship);
        return new ResponseEntity<>("Scholarship created successfully", HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Scholarship> getScholarshipById(@PathVariable String id) {
        Scholarship scholarship = searchScholarship.execute(id);
        return new ResponseEntity<>(scholarship, HttpStatus.OK);
    }

    @GetMapping("/get_all")
    public ResponseEntity<List<Scholarship>> getAllScholarships() {
        List<Scholarship> allScholarship = getAllScholarships.execute();
        return new ResponseEntity<>(allScholarship, HttpStatus.OK);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<String> updateScholarship(@RequestBody ScholarshipDTO scholarship, @PathVariable String id) {
        updateScholarship.execute(scholarship, id);
        return new ResponseEntity<>("Scholarship updated successfully", HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteScholarship(@PathVariable String id) {
        deleteScholarship.execute(id); 
        return new ResponseEntity<>("Scholarship deleted successfully", HttpStatus.OK);
    }
}
