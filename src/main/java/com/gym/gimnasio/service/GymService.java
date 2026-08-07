package com.gym.gimnasio.service;

import com.gym.gimnasio.model.*;
import com.gym.gimnasio.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.*;

@Service
@RequiredArgsConstructor
public class GymService {
    private final ClientRepository clients;
    private final MembershipTypeRepository types;
    private final ClientMembershipRepository memberships;
    private final AttendanceRepository attendances;

    public ClientMembership assignMembership(Long clientId, Long typeId, LocalDate start) {
        var client = clients.findById(clientId).orElseThrow();
        var type = types.findById(typeId).orElseThrow();
        var cm = ClientMembership.builder().cliente(client).membresia(type).fechaInicio(start)
                .fechaVencimiento(start.plusDays(type.getDuracionDias())).build();
        return memberships.save(cm);
    }

    public Attendance registerAttendance(Long clientId) {
        var client = clients.findById(clientId).orElseThrow();
        if (!client.isActivo()) throw new IllegalStateException("Cliente desactivado");
        var membership = memberships.findTopByClienteIdOrderByFechaVencimientoDesc(clientId)
                .orElseThrow(() -> new IllegalStateException("El cliente no tiene membresía"));
        if (membership.getFechaVencimiento().isBefore(LocalDate.now()))
            throw new IllegalStateException("Membresía vencida. Entrada no permitida");
        return attendances.save(Attendance.builder().cliente(client).fechaHora(LocalDateTime.now()).build());
    }
}
