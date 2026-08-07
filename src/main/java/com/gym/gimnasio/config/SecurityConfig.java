package com.gym.gimnasio.config;

import com.gym.gimnasio.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomUserDetailsService uds;

    @Bean PasswordEncoder passwordEncoder(){ return new BCryptPasswordEncoder(); }

    @Bean SecurityFilterChain security(HttpSecurity http) throws Exception {
        http.userDetailsService(uds)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/css/**", "/js/**", "/login", "/h2-console/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated())
            .formLogin(form -> form.loginPage("/login").defaultSuccessUrl("/", true).permitAll())
            .logout(logout -> logout.logoutSuccessUrl("/login?logout"));
        http.csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"));
        http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));
        return http.build();
    }
}
