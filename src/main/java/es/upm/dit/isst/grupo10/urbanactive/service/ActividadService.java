package es.upm.dit.isst.grupo10.urbanactive.service;

import es.upm.dit.isst.grupo10.urbanactive.model.Actividad;
import org.springframework.stereotype.Service;
import es.upm.dit.isst.grupo10.urbanactive.model.Nivel;

import java.util.ArrayList;
import java.util.List;

@Service
public class ActividadService {

    private final List<Actividad> actividades = new ArrayList<>();

    public ActividadService() {
    actividades.add(new Actividad(
            1L,
            "Running",
            "Running por El Retiro",
            "Salida de running por El Retiro",
            new Nivel(10.00),
            "Juan Perez",
            "29/03/2026",
            "14:00",
            "Puerta de O'Donnell, Parque del Retiro",
            40.421683,
            -3.680170,
            12.0,
            10,
            "30 minutos",
            "/img/running.jpg"
    ));

    actividades.add(new Actividad(
            2L,
            "Yoga",
            "Yoga al Aire Libre",
            "Sesión de yoga al aire libre",
            new Nivel(8.00),
            "Lucía Lopez",
            "30/04/2026",
            "18:00",
            "Parque del Oeste",
            40.4264,
            -3.7192,
            9.5,
            5,
            "1 hora",
            "/img/yoga.jpg"
    ));

    actividades.add(new Actividad(
            3L,
            "Ciclismo",
            "Ruta Urbana en Bicicleta",
            "Ruta urbana en bicicleta",
            new Nivel(5.00),
            "Juan Moreno",
            "6/05/2026",
            "19:00",
            "El Pardo",
            40.5186,
            -3.7737,
            0,
            14,
            "1 hora y 30 minutos",
            "/img/ciclismo.jpg"
    ));
}

    public List<Actividad> getActividades() {
        return actividades;
    }

    public Actividad getActividadById(Long id) {
        return actividades.stream()
                .filter(a -> a.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public boolean reservarActividad(Long id) {
        Actividad actividad = getActividadById(id);
        if (actividad != null && actividad.getPlazasDisponibles() > 0) {
            actividad.setPlazasDisponibles(actividad.getPlazasDisponibles() - 1);
            return true;
        }
        return false;
    }
}