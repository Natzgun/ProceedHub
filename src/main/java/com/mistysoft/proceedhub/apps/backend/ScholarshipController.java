package com.mistysoft.proceedhub.apps.backend;

import com.mistysoft.proceedhub.apps.backend.dto.ScholarshipRequest;
import com.mistysoft.proceedhub.apps.backend.dto.ScholarshipResponse;
import com.mistysoft.proceedhub.modules.scholarship.application.*;
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

    public ScholarshipController(CreateScholarship createScholarship, UpdateScholarship updateScholarship,
                                 GetAllScholarships getAllScholarships, SearchScholarship searchScholarship,
                                 DeleteScholarship deleteScholarship) {
        this.createScholarship = createScholarship;
        this.updateScholarship = updateScholarship;
        this.getAllScholarships = getAllScholarships;
        this.searchScholarship = searchScholarship;
        this.deleteScholarship = deleteScholarship;
    }

    @PostMapping("/create")
    public ResponseEntity<ScholarshipResponse> createScholarship(@RequestBody ScholarshipRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ScholarshipResponse.from(createScholarship.execute(request.toChanges())));
    }

    @GetMapping("/{id}")
    public ScholarshipResponse getScholarshipById(@PathVariable String id) {
        return ScholarshipResponse.from(searchScholarship.execute(id));
    }

    @GetMapping("/get_all")
    public List<ScholarshipResponse> getAllScholarships() {
        return getAllScholarships.execute().stream().map(ScholarshipResponse::from).toList();
    }

    @PostMapping("/update/{id}")
    public ScholarshipResponse updateScholarship(@RequestBody ScholarshipRequest request, @PathVariable String id) {
        return ScholarshipResponse.from(updateScholarship.execute(request.toChanges(), id));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteScholarship(@PathVariable String id) {
        deleteScholarship.execute(id);
        return ResponseEntity.noContent().build();
    }
}
