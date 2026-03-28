package es.upm.dit.isst.grupo10.urbanactive.controller;

import es.upm.dit.isst.grupo10.urbanactive.model.Actividad;
import es.upm.dit.isst.grupo10.urbanactive.service.ActividadService;
import es.upm.dit.isst.grupo10.urbanactive.service.ActividadContextService;
import es.upm.dit.isst.grupo10.urbanactive.dto.ActividadContexto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ActividadController {

    private final ActividadService actividadService;
    private final ActividadContextService actividadContextService;

    public ActividadController(ActividadService actividadService, ActividadContextService actividadContextService) {
        this.actividadService = actividadService;
        this.actividadContextService = actividadContextService;
    }

    @GetMapping("/actividades")
    public String listarActividades(Model model) {
        model.addAttribute("actividades", actividadService.getActividades());
        return "actividades";
    }

    @GetMapping("/actividades/{id}")
    public String verDetalle(@PathVariable Long id, Model model) {
        Actividad actividad = actividadService.getActividadById(id);
        if (actividad == null) {
            return "redirect:/actividades";
        }

        ActividadContexto contexto = actividadContextService.getContexto(actividad);

        model.addAttribute("actividad", actividad);
        model.addAttribute("contexto", contexto);
        return "actividad-detalle";
    }

    @PostMapping("/actividades/{id}/reservar")
    public String reservarActividad(@PathVariable Long id, Model model) {
        boolean reservada = actividadService.reservarActividad(id);
        model.addAttribute("actividad", actividadService.getActividadById(id));
        model.addAttribute("mensaje", reservada ? "Reserva realizada correctamente" : "No quedan plazas disponibles");
        return "actividad-detalle";
    }

    @GetMapping("/actividades/{id}/reservar-sesion")
    public String reservarSesion(@PathVariable Long id, Model model) {
        return "redirect:/reservas/actividad/" + id;
    }
}