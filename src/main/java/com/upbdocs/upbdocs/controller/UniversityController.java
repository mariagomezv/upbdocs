package com.upbdocs.upbdocs.controller;

import com.upbdocs.upbdocs.model.University;
import com.upbdocs.upbdocs.service.UniversityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/universities")
@RequiredArgsConstructor
public class UniversityController {

    private final UniversityService universityService;

    @GetMapping
    public ResponseEntity<List<University>> getAllUniversities() {
        return ResponseEntity.ok(universityService.getAllUniversities());
    }

    @GetMapping("/{id}")
    public ResponseEntity<University> getUniversityById(@PathVariable Long id) {
        return universityService.getUniversityById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<University> createUniversity(@RequestBody University university) {
        return ResponseEntity.ok(universityService.createUniversity(university));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<University> updateUniversity(@PathVariable Long id, @RequestBody University university) {
        return universityService.updateUniversity(id, university)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteUniversity(@PathVariable Long id) {
        if (universityService.deleteUniversity(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}