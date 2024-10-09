package com.upbdocs.upbdocs.repository;

import com.upbdocs.upbdocs.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CountryRepository extends JpaRepository<Country, Long> {
    Optional<Country> findByName(String name);
}
