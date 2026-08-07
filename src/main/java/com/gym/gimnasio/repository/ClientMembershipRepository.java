package com.gym.gimnasio.repository;
import com.gym.gimnasio.model.ClientMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.*;
public interface ClientMembershipRepository extends JpaRepository<ClientMembership, Long> {
    Optional<ClientMembership> findTopByClienteIdOrderByFechaVencimientoDesc(Long clienteId);
    long countByFechaVencimientoGreaterThanEqual(LocalDate date);
    long countByFechaVencimientoBefore(LocalDate date);
}
