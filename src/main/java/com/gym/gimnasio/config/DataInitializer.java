package com.gym.gimnasio.config;

import com.gym.gimnasio.model.*;
import com.gym.gimnasio.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final UserRepository users;
    private final MembershipTypeRepository memberships;
    private final PasswordEncoder encoder;

    @Override public void run(String... args) {
        if (users.findByUsername("admin").isEmpty())
            users.save(User.builder().username("admin").password(encoder.encode("Admin123*" )).role(Role.ADMIN).build());
        if (users.findByUsername("cliente").isEmpty())
            users.save(User.builder().username("cliente").password(encoder.encode("Cliente123*" )).role(Role.CLIENTE).build());
        if (memberships.count() == 0) {
            memberships.save(MembershipType.builder().nombre("Mensual").precio(new BigDecimal("25.00")).duracionDias(30).build());
            memberships.save(MembershipType.builder().nombre("Trimestral").precio(new BigDecimal("65.00")).duracionDias(90).build());
            memberships.save(MembershipType.builder().nombre("Anual").precio(new BigDecimal("220.00")).duracionDias(365).build());
        }
    }
}
