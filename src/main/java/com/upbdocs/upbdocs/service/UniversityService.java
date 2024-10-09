package com.upbdocs.upbdocs.service;

import com.upbdocs.upbdocs.model.University;
import com.upbdocs.upbdocs.repository.UniversityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UniversityService {
    private final UniversityRepository universityRepository;

    public List<University> getAllUniversities() {
        return universityRepository.findAll();
    }

    public Optional<University> getUniversityById(Long id) {
        return universityRepository.findById(id);
    }

    @Transactional
    public University createUniversity(University university) {
        return universityRepository.save(university);
    }

    @Transactional
    public Optional<University> updateUniversity(Long id, University updatedUniversity) {
        return universityRepository.findById(id)
                .map(university -> {
                    university.setName(updatedUniversity.getName());
                    university.setCountry(updatedUniversity.getCountry());
                    university.setRegion(updatedUniversity.getRegion());
                    return universityRepository.save(university);
                });
    }

    @Transactional
    public boolean deleteUniversity(Long id) {
        return universityRepository.findById(id)
                .map(university -> {
                    universityRepository.delete(university);
                    return true;
                })
                .orElse(false);
    }
}