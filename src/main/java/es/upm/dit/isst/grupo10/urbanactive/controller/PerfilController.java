package es.upm.dit.isst.grupo10.urbanactive.controller;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class PerfilController {

    @GetMapping("/usuarios/{id}")
    public String verPerfilUsuario(@PathVariable Long id, Model model) {
        return "perfil-usuario";
    }

    @GetMapping("/organizaciones/{id}")
    public String verPerfilOrganizacion(@PathVariable Long id, Model model) {
        return "perfil-organizacion";
    }

    @PostMapping("/usuarios/{id}/seguir")
    public String seguirUsuario(@PathVariable Long id) {
        return "redirect:/usuarios/" + id;
    }

    @PostMapping("/organizaciones/{id}/seguir")
    public String seguirOrganizacion(@PathVariable Long id) {
        return "redirect:/organizaciones/" + id;
    }
}