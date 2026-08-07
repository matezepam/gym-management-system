package com.gym.gimnasio.controller;

import com.gym.gimnasio.model.Attendance;
import com.gym.gimnasio.model.Client;
import com.gym.gimnasio.model.ClientMembership;
import com.gym.gimnasio.model.MembershipType;
import com.gym.gimnasio.model.Payment;

import com.gym.gimnasio.repository.AttendanceRepository;
import com.gym.gimnasio.repository.ClientMembershipRepository;
import com.gym.gimnasio.repository.ClientRepository;
import com.gym.gimnasio.repository.MembershipTypeRepository;
import com.gym.gimnasio.repository.PaymentRepository;

import com.gym.gimnasio.service.GymService;

import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
public class WebController {

    private final ClientRepository clients;
    private final MembershipTypeRepository types;
    private final ClientMembershipRepository memberships;
    private final AttendanceRepository attendances;
    private final PaymentRepository payments;
    private final GymService service;



    @GetMapping("/login")
    public String login() {
        return "login";
    }


    @GetMapping("/")
    public String home(Authentication auth) {

        boolean admin = auth.getAuthorities()
                .stream()
                .anyMatch(a ->
                        a.getAuthority().equals("ROLE_ADMIN")
                );

        if (admin) {
            return "redirect:/admin/dashboard";
        }

        return "redirect:/cliente";
    }

    @GetMapping("/cliente")
    public String clientHome(Model model) {

        model.addAttribute(
                "mensaje",
                "Sesión de cliente iniciada correctamente."
        );

        return "cliente";
    }


    @GetMapping("/admin/dashboard")
    public String dashboard(Model model) {

        LocalDate today = LocalDate.now();

        model.addAttribute(
                "clientes",
                clients.countByActivoTrue()
        );

        model.addAttribute(
                "activas",
                memberships
                        .countByFechaVencimientoGreaterThanEqual(today)
        );

        model.addAttribute(
                "vencidas",
                memberships
                        .countByFechaVencimientoBefore(today)
        );

        model.addAttribute(
                "asistencias",
                attendances.count()
        );

        model.addAttribute(
                "pagos",
                payments.count()
        );

        return "dashboard";
    }


    @GetMapping("/admin/clientes")
    public String clientes(
            @RequestParam(defaultValue = "") String q,
            Model model) {

        if (q.isBlank()) {

            model.addAttribute(
                    "clientes",
                    clients.findAll()
            );

        } else {

            model.addAttribute(
                    "clientes",
                    clients
                            .findByNombreContainingIgnoreCaseOrCedulaContainingIgnoreCase(
                                    q,
                                    q
                            )
            );
        }

        model.addAttribute("q", q);

        return "clientes";
    }


    @GetMapping("/admin/clientes/nuevo")
    public String nuevoCliente(Model model) {

        model.addAttribute(
                "cliente",
                new Client()
        );

        return "cliente-form";
    }


    @GetMapping("/admin/clientes/{id}/editar")
    public String editarCliente(
            @PathVariable Long id,
            Model model) {

        Client client = clients
                .findById(id)
                .orElseThrow();

        model.addAttribute(
                "cliente",
                client
        );

        return "cliente-form";
    }


    @PostMapping("/admin/clientes/guardar")
    public String guardarCliente(
            Client client,
            RedirectAttributes ra) {

        try {

            if (client.getId() != null) {

                Client old = clients
                        .findById(client.getId())
                        .orElseThrow();

                client.setActivo(
                        old.isActivo()
                );

            } else {

                client.setActivo(true);
            }

            clients.save(client);

            ra.addFlashAttribute(
                    "ok",
                    "Cliente guardado correctamente"
            );

        } catch (Exception e) {

            ra.addFlashAttribute(
                    "error",
                    "No se pudo guardar el cliente: "
                            + e.getMessage()
            );
        }

        return "redirect:/admin/clientes";
    }


    @PostMapping("/admin/clientes/{id}/estado")
    public String cambiarEstadoCliente(
            @PathVariable Long id,
            RedirectAttributes ra) {

        try {

            Client client = clients
                    .findById(id)
                    .orElseThrow();

            client.setActivo(
                    !client.isActivo()
            );

            clients.save(client);

            if (client.isActivo()) {

                ra.addFlashAttribute(
                        "ok",
                        "Cliente activado correctamente"
                );

            } else {

                ra.addFlashAttribute(
                        "ok",
                        "Cliente desactivado correctamente"
                );
            }

        } catch (Exception e) {

            ra.addFlashAttribute(
                    "error",
                    "No se pudo cambiar el estado del cliente"
            );
        }

        return "redirect:/admin/clientes";
    }




    @GetMapping("/admin/membresias")
    public String membresias(Model model) {

        model.addAttribute(
                "tipos",
                types.findAll()
        );

        model.addAttribute(
                "asignadas",
                memberships.findAll()
        );

        model.addAttribute(
                "clientes",
                clients.findAll()
        );

        return "membresias";
    }


    @PostMapping("/admin/membresias/tipo")
    public String crearTipo(
            @RequestParam String nombre,
            @RequestParam BigDecimal precio,
            @RequestParam Integer duracionDias,
            RedirectAttributes ra) {

        try {

            String nombreLimpio = nombre.trim();

            if (types.existsByNombreIgnoreCase(nombreLimpio)) {

                ra.addFlashAttribute(
                        "error",
                        "Ya existe una membresía llamada "
                                + nombreLimpio
                );

                return "redirect:/admin/membresias";
            }

            MembershipType tipo =
                    MembershipType.builder()
                            .nombre(nombreLimpio)
                            .precio(precio)
                            .duracionDias(duracionDias)
                            .build();

            types.save(tipo);

            ra.addFlashAttribute(
                    "ok",
                    "Membresía creada correctamente"
            );

        } catch (Exception e) {

            ra.addFlashAttribute(
                    "error",
                    "No se pudo crear la membresía: "
                            + e.getMessage()
            );
        }

        return "redirect:/admin/membresias";
    }


    @PostMapping("/admin/membresias/asignar")
    public String asignarMembresia(
            @RequestParam Long clienteId,
            @RequestParam Long tipoId,
            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate fechaInicio,
            RedirectAttributes ra) {

        try {

            service.assignMembership(
                    clienteId,
                    tipoId,
                    fechaInicio
            );

            ra.addFlashAttribute(
                    "ok",
                    "Membresía asignada correctamente"
            );

        } catch (Exception e) {

            ra.addFlashAttribute(
                    "error",
                    "No se pudo asignar la membresía: "
                            + e.getMessage()
            );
        }

        return "redirect:/admin/membresias";
    }


    @GetMapping("/admin/asistencias")
    public String asistencias(Model model) {

        model.addAttribute(
                "clientes",
                clients.findAll()
        );

        model.addAttribute(
                "asistencias",
                attendances.findAll()
        );

        return "asistencias";
    }


    @PostMapping("/admin/asistencias/registrar")
    public String registrarAsistencia(
            @RequestParam Long clienteId,
            RedirectAttributes ra) {

        try {

            service.registerAttendance(
                    clienteId
            );

            ra.addFlashAttribute(
                    "ok",
                    "Entrada registrada correctamente"
            );

        } catch (Exception e) {

            ra.addFlashAttribute(
                    "error",
                    e.getMessage()
            );
        }

        return "redirect:/admin/asistencias";
    }

    @GetMapping("/admin/pagos")
    public String pagos(Model model) {

        model.addAttribute(
                "clientes",
                clients.findAll()
        );

        model.addAttribute(
                "pagos",
                payments.findAll()
        );

        return "pagos";
    }


    @PostMapping("/admin/pagos")
    public String registrarPago(
            @RequestParam Long clienteId,
            @RequestParam BigDecimal monto,
            @RequestParam String descripcion,
            RedirectAttributes ra) {

        try {

            Client client = clients
                    .findById(clienteId)
                    .orElseThrow();

            Payment payment =
                    Payment.builder()
                            .cliente(client)
                            .monto(monto)
                            .fechaPago(LocalDate.now())
                            .descripcion(descripcion.trim())
                            .build();

            payments.save(payment);

            ra.addFlashAttribute(
                    "ok",
                    "Pago registrado correctamente"
            );

        } catch (Exception e) {

            ra.addFlashAttribute(
                    "error",
                    "No se pudo registrar el pago: "
                            + e.getMessage()
            );
        }

        return "redirect:/admin/pagos";
    }
}