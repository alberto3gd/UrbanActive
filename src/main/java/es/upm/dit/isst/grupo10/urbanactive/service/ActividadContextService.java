package es.upm.dit.isst.grupo10.urbanactive.service;

import es.upm.dit.isst.grupo10.urbanactive.model.Actividad;
import es.upm.dit.isst.grupo10.urbanactive.dto.ActividadContexto;
import es.upm.dit.isst.grupo10.urbanactive.dto.GeoPoint;
import es.upm.dit.isst.grupo10.urbanactive.dto.WeatherInfo;
import es.upm.dit.isst.grupo10.urbanactive.dto.TrafficInfo;
import org.springframework.stereotype.Service;

@Service
public class ActividadContextService {

    private final GeocodingService geocodingService;
    private final AemetService aemetService;
    private final TrafficService trafficService;

    public ActividadContextService(
            GeocodingService geocodingService,
            AemetService aemetService,
            TrafficService trafficService) {
        this.geocodingService = geocodingService;
        this.aemetService = aemetService;
        this.trafficService = trafficService;
    }

    public ActividadContexto getContexto(Actividad actividad, GeoPoint userPoint) {
        GeoPoint puntoActividad = null;

        if (actividad.getLatitud() != null && actividad.getLongitud() != null) {
            puntoActividad = new GeoPoint(
                    actividad.getLatitud(),
                    actividad.getLongitud(),
                    actividad.getUbicacion()
            );
        } else {
            puntoActividad = geocodingService.buscar(actividad.getUbicacion() + ", Madrid, España");
        }

        WeatherInfo weather = aemetService.getWeatherMadrid(actividad.getFecha());
        TrafficInfo traffic = trafficService.calcularNivelSimple(puntoActividad);

        return new ActividadContexto(puntoActividad, weather, traffic);
    }
}