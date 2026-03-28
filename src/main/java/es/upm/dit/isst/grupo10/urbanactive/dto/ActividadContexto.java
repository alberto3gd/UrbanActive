package es.upm.dit.isst.grupo10.urbanactive.dto;

public record ActividadContexto(
        GeoPoint punto,
        WeatherInfo weather,
        TrafficInfo traffic
) {}