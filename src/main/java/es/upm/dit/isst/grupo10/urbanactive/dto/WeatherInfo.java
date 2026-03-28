package es.upm.dit.isst.grupo10.urbanactive.dto;

public record WeatherInfo(
        String estadoCielo,
        String temperatura,
        String viento,
        String probabilidadLluvia,
        String aviso
) {}