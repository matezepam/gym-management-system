package com.gym.gimnasio.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name="cliente_membresias")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ClientMembership {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional=false)
    private Client cliente;
    @ManyToOne(optional=false)
    private MembershipType membresia;
    @Column(nullable=false)
    private LocalDate fechaInicio;
    @Column(nullable=false)
    private LocalDate fechaVencimiento;
}
