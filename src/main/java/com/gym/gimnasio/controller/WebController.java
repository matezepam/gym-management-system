package com.gym.gimnasio.controller;

import com.gym.gimnasio.model.*;
import com.gym.gimnasio.repository.*;
import com.gym.gimnasio.service.GymService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping("/login") String login(){ return "login"; }

    @GetMapping("/") String home(Authentication auth){
        boolean admin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        return admin ? "redirect:/admin/dashboard" : "redirect:/cliente";
    }

    @GetMapping("/cliente") String clientHome(Model model){
        model.addAttribute("mensaje", "Sesión de cliente iniciada correctamente.");
        return "cliente";
    }

    @GetMapping("/admin/dashboard") String dashboard(Model model){
        LocalDate today = LocalDate.now();
        model.addAttribute("clientes", clients.countByActivoTrue());
        model.addAttribute("activas", memberships.countByFechaVencimientoGreaterThanEqual(today));
        model.addAttribute("vencidas", memberships.countByFechaVencimientoBefore(today));
        model.addAttribute("asistencias", attendances.count());
        model.addAttribute("pagos", payments.count());
        return "dashboard";
    }

    @GetMapping("/admin/clientes") String clientes(@RequestParam(defaultValue="") String q, Model model){
        model.addAttribute("clientes", q.isBlank() ? clients.findAll() : clients.findByNombreContainingIgnoreCaseOrCedulaContainingIgnoreCase(q,q));
        model.addAttribute("q", q);
        return "clientes";
    }

    @GetMapping("/admin/clientes/nuevo") String nuevo(Model model){ model.addAttribute("cliente", new Client()); return "cliente-form"; }
    @GetMapping("/admin/clientes/{id}/editar") String editar(@PathVariable Long id, Model model){ model.addAttribute("cliente", clients.findById(id).orElseThrow()); return "cliente-form"; }
    @PostMapping("/admin/clientes/guardar") String guardar(Client client){ if(client.getId()!=null){ var old=clients.findById(client.getId()).orElseThrow(); client.setActivo(old.isActivo()); } else client.setActivo(true); clients.save(client); return "redirect:/admin/clientes"; }
    @PostMapping("/admin/clientes/{id}/estado") String estado(@PathVariable Long id){ var c=clients.findById(id).orElseThrow(); c.setActivo(!c.isActivo()); clients.save(c); return "redirect:/admin/clientes"; }

    @GetMapping("/admin/membresias") String membresias(Model model){
        model.addAttribute("tipos", types.findAll()); model.addAttribute("asignadas", memberships.findAll()); model.addAttribute("clientes", clients.findAll()); return "membresias";
    }
    @PostMapping("/admin/membresias/tipo") String crearTipo(@RequestParam String nombre,@RequestParam BigDecimal precio,@RequestParam Integer duracionDias){ types.save(MembershipType.builder().nombre(nombre).precio(precio).duracionDias(duracionDias).build()); return "redirect:/admin/membresias"; }
    @PostMapping("/admin/membresias/asignar") String asignar(@RequestParam Long clienteId,@RequestParam Long tipoId,@RequestParam LocalDate fechaInicio){ service.assignMembership(clienteId,tipoId,fechaInicio); return "redirect:/admin/membresias"; }

    @GetMapping("/admin/asistencias") String asistencias(Model model){ model.addAttribute("clientes", clients.findAll()); model.addAttribute("asistencias", attendances.findAll()); return "asistencias"; }
    @PostMapping("/admin/asistencias/registrar") String registrar(@RequestParam Long clienteId, RedirectAttributes ra){
        try { service.registerAttendance(clienteId); ra.addFlashAttribute("ok","Entrada registrada correctamente"); }
        catch(Exception e){ ra.addFlashAttribute("error",e.getMessage()); }
        return "redirect:/admin/asistencias";
    }

    @GetMapping("/admin/pagos") String pagos(Model model){ model.addAttribute("clientes", clients.findAll()); model.addAttribute("pagos", payments.findAll()); return "pagos"; }
    @PostMapping("/admin/pagos") String pago(@RequestParam Long clienteId,@RequestParam BigDecimal monto,@RequestParam String descripcion){
        var c=clients.findById(clienteId).orElseThrow(); payments.save(Payment.builder().cliente(c).monto(monto).fechaPago(LocalDate.now()).descripcion(descripcion).build()); return "redirect:/admin/pagos";
    }
}
