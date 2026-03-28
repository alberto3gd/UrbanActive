package es.upm.dit.isst.grupo10.urbanactive.dto;

public record TrafficInfo(
        String nivel,
        int incidenciasCercanas,
        String resumen,
        double distanciaKm,
        String duracionConTrafico,
        String duracionSinTrafico
) {}