package com.example.demo.configuration;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SecurityConfig
{
    String[] PUBLIC_ENDPOINT_GET = {"/api/users/**"};
    String[] PUBLIC_ENDPOINT_POST = {"/api/users/**", "/api/auth/**"};
    String[] PUBLIC_ENDPOINT_PUT = {"/api/users/**"};
    String[] PUBLIC_ENDPOINT_DELETE = {};

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception
    {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request ->
                        request
                                .requestMatchers(HttpMethod.GET, PUBLIC_ENDPOINT_GET).permitAll()
                                .requestMatchers(HttpMethod.POST, PUBLIC_ENDPOINT_POST).permitAll()
                                .requestMatchers(HttpMethod.PUT, PUBLIC_ENDPOINT_PUT).permitAll()
                                .requestMatchers(HttpMethod.DELETE, PUBLIC_ENDPOINT_DELETE).permitAll()
                                .anyRequest().authenticated()
                );

        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder(10);
    }
}
