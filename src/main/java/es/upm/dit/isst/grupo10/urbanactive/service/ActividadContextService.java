package es.upm.dit.isst.grupo10.urbanactive.service;

import es.upm.dit.isst.grupo10.urbanactive.dto.ActividadContexto;
import es.upm.dit.isst.grupo10.urbanactive.dto.GeoPoint;
import es.upm.dit.isst.grupo10.urbanactive.dto.TrafficInfo;
import es.upm.dit.isst.grupo10.urbanactive.dto.WeatherInfo;
import es.upm.dit.isst.grupo10.urbanactive.model.Actividad;
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
        GeoPoint puntoActividad;

        if (actividad.getLatitud() != 0.0 && actividad.getLongitud() != 0.0) {
            puntoActividad = new GeoPoint(
                    actividad.getLatitud(),
                    actividad.getLongitud(),
                    actividad.getUbicacion()
            );
        } else {
            puntoActividad = geocodingService.buscar(actividad.getUbicacion() + ", Madrid, España");
        }

        WeatherInfo weather = aemetService.getWeatherMadrid(actividad.getFecha());

        TrafficInfo traffic;
        if (userPoint != null && puntoActividad != null) {
            traffic = trafficService.calcularTraficoTrayecto(userPoint, puntoActividad);
        } else if (puntoActividad != null) {
            traffic = new TrafficInfo(
                    "No disponible",
                    0,
                    "Permite la ubicación para ver el trayecto estimado",
                    0.0,
                    "-",
                    "-"
            );
        } else {
            traffic = new TrafficInfo(
                    "No disponible",
                    0,
                    "No se pudo obtener la ubicación de la actividad",
                    0.0,
                    "-",
                    "-"
            );
        }

        return new ActividadContexto(puntoActividad, weather, traffic);
    }
}