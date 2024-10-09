package com.upbdocs.upbdocs.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.upbdocs.upbdocs.model.Country;
import com.upbdocs.upbdocs.model.Region;
import com.upbdocs.upbdocs.model.University;
import com.upbdocs.upbdocs.repository.CountryRepository;
import com.upbdocs.upbdocs.repository.RegionRepository;
import com.upbdocs.upbdocs.repository.UniversityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class UniversityInitializationService {

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private UniversityRepository universityRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void initializeUniversities() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        TypeReference<List<Map<String, String>>> typeReference = new TypeReference<>() {};

        List<Map<String, String>> universitiesData = objectMapper.readValue(
                new ClassPathResource("./static/universities.json").getInputStream(),
                typeReference
        );

        for (Map<String, String> universityData : universitiesData) {
            String institutionName = universityData.get("institution");
            String locationName = universityData.get("Location");
            String regionName = universityData.get("Region");

            Country country = countryRepository.findByName(locationName)
                    .orElseGet(() -> countryRepository.save(new Country(null, locationName)));

            Region region = regionRepository.findByName(regionName)
                    .orElseGet(() -> regionRepository.save(new Region(null, regionName)));

            University university = new University();
            university.setName(institutionName);
            university.setCountry(country);
            university.setRegion(region);

            universityRepository.save(university);
        }
    }
}