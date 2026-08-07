package com.gym.gimnasio.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name="tipos_membresia")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MembershipType {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false, unique=true)
    private String nombre;
    @Column(nullable=false)
    private BigDecimal precio;
    @Column(nullable=false)
    private Integer duracionDias;
}
