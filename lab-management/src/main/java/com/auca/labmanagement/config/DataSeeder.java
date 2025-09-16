package com.auca.labmanagement.config;

import com.auca.labmanagement.domain.*;
import com.auca.labmanagement.repository.LabRepository;
import com.auca.labmanagement.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.EnumSet;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedInitial(UserRepository userRepository,
                                  LabRepository labRepository,
                                  PasswordEncoder passwordEncoder) {
        return args -> {
            // Basic guard to allow disabling seeding via property
            var seedEnabled = Boolean.parseBoolean(System.getProperty("app.seed", System.getenv().getOrDefault("APP_SEED", "true")));
            if (!seedEnabled) return;
            if (userRepository.count() == 0) {
                var admin = User.builder()
                        .username("admin")
                        .email("admin@auca.local")
                        .passwordHash(passwordEncoder.encode("admin123"))
                        .roles(EnumSet.of(UserRole.ADMIN))
                        .enabled(true)
                        .build();
                userRepository.save(admin);

                var manager = User.builder()
                        .username("manager")
                        .email("manager@auca.local")
                        .passwordHash(passwordEncoder.encode("manager123"))
                        .roles(EnumSet.of(UserRole.LAB_MANAGER))
                        .enabled(true)
                        .build();
                userRepository.save(manager);

                var instructor = User.builder()
                        .username("instructor")
                        .email("instructor@auca.local")
                        .passwordHash(passwordEncoder.encode("instructor123"))
                        .roles(EnumSet.of(UserRole.INSTRUCTOR))
                        .enabled(true)
                        .build();
                userRepository.save(instructor);

                var student = User.builder()
                        .username("student")
                        .email("student@auca.local")
                        .passwordHash(passwordEncoder.encode("student123"))
                        .roles(EnumSet.of(UserRole.STUDENT))
                        .enabled(true)
                        .build();
                userRepository.save(student);
            }

            if (labRepository.count() == 0) {
                var manager = userRepository.findByUsername("manager").orElse(null);
                labRepository.save(Lab.builder().name("Main Computer Lab").location("Building A").capacity(60).type(LabType.MAIN_COMPUTER_LAB).assignedManager(manager).build());
                labRepository.save(Lab.builder().name("Lab 104").location("104").capacity(30).type(LabType.EXT_104).assignedManager(manager).build());
                labRepository.save(Lab.builder().name("Lab 108").location("108").capacity(30).type(LabType.EXT_108).assignedManager(manager).build());
                labRepository.save(Lab.builder().name("Lab 204").location("204").capacity(30).type(LabType.EXT_204).assignedManager(manager).build());
                labRepository.save(Lab.builder().name("Lab 209").location("209").capacity(30).type(LabType.EXT_209).assignedManager(manager).build());
                labRepository.save(Lab.builder().name("Lab 310").location("310").capacity(30).type(LabType.EXT_310).assignedManager(manager).build());
                labRepository.save(Lab.builder().name("English Lab").location("Language Center").capacity(25).type(LabType.ENGLISH_LAB).assignedManager(manager).build());
            }
        };
    }
}

