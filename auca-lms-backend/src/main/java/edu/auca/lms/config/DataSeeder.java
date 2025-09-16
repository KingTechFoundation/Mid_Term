package edu.auca.lms.config;

import edu.auca.lms.lab.Lab;
import edu.auca.lms.lab.LabRepository;
import edu.auca.lms.lab.LabType;
import edu.auca.lms.user.Role;
import edu.auca.lms.user.User;
import edu.auca.lms.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seed(UserRepository users, PasswordEncoder encoder, LabRepository labs) {
        return args -> {
            if (users.count() == 0) {
                User admin = User.builder()
                        .username("admin")
                        .passwordHash(encoder.encode("admin123"))
                        .fullName("System Admin")
                        .email("admin@auca.edu")
                        .role(Role.ADMIN)
                        .enabled(true)
                        .build();
                users.save(admin);

                User manager = User.builder()
                        .username("manager")
                        .passwordHash(encoder.encode("manager123"))
                        .fullName("Main Lab Manager")
                        .email("manager@auca.edu")
                        .role(Role.LAB_MANAGER)
                        .enabled(true)
                        .build();
                users.save(manager);

                User instructor = User.builder()
                        .username("instructor")
                        .passwordHash(encoder.encode("instructor123"))
                        .fullName("CS Instructor")
                        .email("instructor@auca.edu")
                        .role(Role.INSTRUCTOR)
                        .enabled(true)
                        .build();
                users.save(instructor);

                User student = User.builder()
                        .username("student")
                        .passwordHash(encoder.encode("student123"))
                        .fullName("CS Student")
                        .email("student@auca.edu")
                        .role(Role.STUDENT)
                        .enabled(true)
                        .build();
                users.save(student);
            }

            if (labs.count() == 0) {
                labs.save(Lab.builder().name("Main Computer Lab").location("Building A").capacity(60).type(LabType.MAIN_COMPUTER_LAB).build());
                labs.save(Lab.builder().name("Extension 104").location("Room 104").capacity(30).type(LabType.EXT_104).build());
                labs.save(Lab.builder().name("Extension 108").location("Room 108").capacity(30).type(LabType.EXT_108).build());
                labs.save(Lab.builder().name("Extension 204").location("Room 204").capacity(30).type(LabType.EXT_204).build());
                labs.save(Lab.builder().name("Extension 209").location("Room 209").capacity(30).type(LabType.EXT_209).build());
                labs.save(Lab.builder().name("Extension 310").location("Room 310").capacity(30).type(LabType.EXT_310).build());
                labs.save(Lab.builder().name("English Lab").location("Language Center").capacity(25).type(LabType.ENGLISH_LAB).build());
            }
        };
    }
}

