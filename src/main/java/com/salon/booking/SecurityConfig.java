package com.salon.booking;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/book", "/status/**", "/login", "/error").permitAll()
                        .requestMatchers("/stylist/**").hasRole("STYLIST")
                        .anyRequest().denyAll())
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/stylist", true)
                        .permitAll())
                .logout(logout -> logout.logoutSuccessUrl("/"));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(
            PasswordEncoder encoder,
            @Value("${salon.stylist.username}") String username,
            @Value("${salon.stylist.password}") String password) {
        UserDetails stylist = User.withUsername(username)
                .password(encoder.encode(password))
                .roles("STYLIST")
                .build();
        return new InMemoryUserDetailsManager(stylist);
    }
}