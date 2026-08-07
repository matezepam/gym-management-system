package com.gym.gimnasio.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name="asistencias")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Attendance {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional=false)
    private Client cliente;
    @Column(nullable=false)
    private LocalDateTime fechaHora;
}
