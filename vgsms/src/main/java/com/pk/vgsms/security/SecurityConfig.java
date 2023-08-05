package com.pk.vgsms.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain web(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(configurer ->
                        configurer.requestMatchers("/").permitAll()
                                .requestMatchers("/v1/login").permitAll()
                                .requestMatchers("/v1/register").permitAll()
                                .anyRequest().authenticated())
                .formLogin(configurer -> configurer.loginPage("/v1/login"))
                .logout(configurer -> configurer
                        .logoutUrl("/v1/logout")
                        .logoutSuccessUrl("/v1/login?logout")
                        .permitAll())
                .build();
    }
}
