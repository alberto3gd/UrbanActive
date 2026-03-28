package es.upm.dit.isst.grupo10.urbanactive.controller;

import es.upm.dit.isst.grupo10.urbanactive.dto.ActividadContexto;
import es.upm.dit.isst.grupo10.urbanactive.dto.GeoPoint;
import es.upm.dit.isst.grupo10.urbanactive.model.Actividad;
import es.upm.dit.isst.grupo10.urbanactive.service.ActividadContextService;
import es.upm.dit.isst.grupo10.urbanactive.service.ActividadService;
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
    public String verDetalle(@PathVariable Long id,
                             @RequestParam(required = false) Double userLat,
                             @RequestParam(required = false) Double userLon,
                             @RequestParam(required = false) String reserva,
                             Model model) {

        Actividad actividad = actividadService.getActividadById(id);
        if (actividad == null) {
            return "redirect:/actividades";
        }

        GeoPoint userPoint = null;
        if (userLat != null && userLon != null) {
            userPoint = new GeoPoint(userLat, userLon, "Tu ubicación");
        }

        ActividadContexto contexto = actividadContextService.getContexto(actividad, userPoint);

        model.addAttribute("actividad", actividad);
        model.addAttribute("contexto", contexto);
        model.addAttribute("userPoint", userPoint);

        if ("ok".equals(reserva)) {
            model.addAttribute("mensaje", "Reserva realizada correctamente");
        } else if ("error".equals(reserva)) {
            model.addAttribute("mensaje", "No quedan plazas disponibles");
        }

        return "actividad-detalle";
    }

    @PostMapping("/actividades/{id}/reservar")
    public String reservarActividad(@PathVariable Long id,
                                    @RequestParam(required = false) Double userLat,
                                    @RequestParam(required = false) Double userLon) {

        boolean reservada = actividadService.reservarActividad(id);

        String redirect = reservada
                ? "redirect:/actividades/" + id + "?reserva=ok"
                : "redirect:/actividades/" + id + "?reserva=error";

        if (userLat != null && userLon != null) {
            redirect += "&userLat=" + userLat + "&userLon=" + userLon;
        }

        return redirect;
    }

    @GetMapping("/actividades/{id}/reservar-sesion")
    public String reservarSesion(@PathVariable Long id) {
        return "redirect:/reservas/actividad/" + id;
    }
}