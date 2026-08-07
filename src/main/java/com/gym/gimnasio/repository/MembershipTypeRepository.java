package com.gym.gimnasio.repository;

import com.gym.gimnasio.model.MembershipType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MembershipTypeRepository
        extends JpaRepository<MembershipType, Long> {

    boolean existsByNombreIgnoreCase(String nombre);
}