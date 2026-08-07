package com.gym.gimnasio.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="clientes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Client {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false)
    private String nombre;
    @Column(unique=true)
    private String cedula;
    private String telefono;
    private String email;
    @Builder.Default
    private boolean activo = true;
}
