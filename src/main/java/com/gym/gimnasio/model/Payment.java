package com.gym.gimnasio.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name="pagos")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Payment {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional=false)
    private Client cliente;
    @Column(nullable=false)
    private BigDecimal monto;
    @Column(nullable=false)
    private LocalDate fechaPago;
    private String descripcion;
}
