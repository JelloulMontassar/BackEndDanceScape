package com.dance.mo.Config;

import com.dance.mo.Entities.Role;
import com.dance.mo.Repositories.UserRepository;
import com.dance.mo.Entities.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Initialize implements CommandLineRunner {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;
    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public void run(String... args) throws Exception{




            User adminUser = userRepository.getUserByEmail("admin@admin.com");
            if (adminUser == null){
                adminUser = new User();
                adminUser.setFirstName("admin");
                adminUser.setLastName("admin");
                adminUser.setEmail("admin@admin.com");
                adminUser.setPassword(passwordEncoder.bCryptPasswordEncoder().encode("admin"));
                adminUser.setRole(Role.ADMIN);
                adminUser.setEnabled(true);
                userRepository.save(adminUser);
                // Generate token for adminUser
                //String jwtToken = jwtService.generateToken(adminUser);

            }

        }
       }


