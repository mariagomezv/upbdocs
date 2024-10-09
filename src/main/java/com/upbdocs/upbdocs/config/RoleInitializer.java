package com.upbdocs.upbdocs.config;

import com.upbdocs.upbdocs.model.ERole;
import com.upbdocs.upbdocs.model.Role;
import com.upbdocs.upbdocs.repository.RoleRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@Component
public class RoleInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (roleRepository.count() == 0) {
            for (ERole role : ERole.values()) {
                Role r = new Role();
                r.setName(role);
                roleRepository.save(r);
            }
        }
    }
}
