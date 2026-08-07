package com.gym.gimnasio.repository;
import com.gym.gimnasio.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ClientRepository extends JpaRepository<Client, Long> {
    List<Client> findByNombreContainingIgnoreCaseOrCedulaContainingIgnoreCase(String nombre, String cedula);
    long countByActivoTrue();
}
