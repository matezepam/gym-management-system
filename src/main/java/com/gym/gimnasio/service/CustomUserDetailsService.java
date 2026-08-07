package com.gym.gimnasio.service;

import com.gym.gimnasio.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository repo;
    @Override public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var u = repo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        return User.withUsername(u.getUsername()).password(u.getPassword()).roles(u.getRole().name()).build();
    }
}
