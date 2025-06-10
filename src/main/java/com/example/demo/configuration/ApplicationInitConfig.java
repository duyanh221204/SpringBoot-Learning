package com.example.demo.configuration;

import com.example.demo.entity.RoleEntity;
import com.example.demo.entity.UserEntity;
import com.example.demo.enums.Role;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationInitConfig
{
    UserRepository userRepository;
    RoleRepository roleRepository;

    @NonFinal
    @Value("${admin_username}")
    String adminUsername;

    @NonFinal
    @Value("${admin_password}")
    String adminPassword;

    @Bean
    public ApplicationRunner applicationRunner(PasswordEncoder passwordEncoder)
    {
        return args ->
        {
            List<RoleEntity> roles = roleRepository.findAll();
            if (roles.isEmpty())
            {
                RoleEntity adminRole = RoleEntity.builder()
                        .name(Role.ADMIN.name())
                        .build();

                RoleEntity userRole = RoleEntity.builder()
                        .name(Role.USER.name())
                        .build();

                roles.add(adminRole);
                roles.add(userRole);
                roleRepository.saveAllAndFlush(roles);

                UserEntity admin = UserEntity.builder()
                        .username(adminUsername)
                        .password(passwordEncoder.encode(adminPassword))
                        .firstName(adminUsername)
                        .lastName(adminUsername)
                        .roles(new HashSet<>())
                        .build();
                admin.getRoles().add(adminRole);
                userRepository.save(admin);
            }
        };
    }
}
