package com.upbdocs.upbdocs.repository;

import com.upbdocs.upbdocs.model.University;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UniversityRepository extends JpaRepository<University, Long> {
    Optional<University> findByName(String name);
}
