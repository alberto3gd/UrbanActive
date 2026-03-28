package es.upm.dit.isst.grupo10.urbanactive.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.upm.dit.isst.grupo10.urbanactive.dto.GeoPoint;
import es.upm.dit.isst.grupo10.urbanactive.dto.TrafficInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class TrafficService {

    @Value("${openrouteservice.api-key:}")
    private String apiKey;

    private final RestClient restClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TrafficService(RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("https://api.openrouteservice.org")
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept", "application/json")
                .defaultHeader("User-Agent", "UrbanActive/1.0 (urbanactive@upm.es)")
                .build();
    }

    public TrafficInfo calcularNivelSimple(GeoPoint punto) {
        if (punto == null) {
            return new TrafficInfo("No disponible", 0, "Sin ubicación", 0.0, "-", "-");
        }

        return new TrafficInfo(
                "No disponible",
                0,
                "Usa el trayecto desde tu ubicación para calcular la ruta",
                0.0,
                "-",
                "-"
        );
    }

    public TrafficInfo calcularTraficoTrayecto(GeoPoint origen, GeoPoint destino) {
        if (origen == null || destino == null) {
            return new TrafficInfo(
                    "No disponible",
                    0,
                    "Faltan ubicaciones",
                    0.0,
                    "-",
                    "-"
            );
        }

        if (apiKey == null || apiKey.isBlank()) {
            return new TrafficInfo(
                    "No disponible",
                    0,
                    "API de OpenRouteService no configurada",
                    0.0,
                    "-",
                    "-"
            );
        }

        try {
            String body = """
                    {
                      "coordinates": [
                        [%s, %s],
                        [%s, %s]
                      ]
                    }
                    """.formatted(
                    origen.lon(), origen.lat(),
                    destino.lon(), destino.lat()
            );

            String response = restClient.post()
                    .uri("/v2/directions/driving-car")
                    .header("Authorization", apiKey)
                    .body(body)
                    .retrieve()
                    .body(String.class);

            JsonNode root = objectMapper.readTree(response);
            JsonNode routes = root.path("routes");

            if (!routes.isArray() || routes.isEmpty()) {
                return new TrafficInfo(
                        "No disponible",
                        0,
                        "No se encontró una ruta",
                        0.0,
                        "-",
                        "-"
                );
            }

            JsonNode summary = routes.get(0).path("summary");

            double distanciaMetros = summary.path("distance").asDouble(0.0);
            double duracionSegundos = summary.path("duration").asDouble(0.0);

            double distanciaKm = distanciaMetros / 1000.0;
            long segundos = Math.round(duracionSegundos);

            String nivel = calcularNivel(distanciaKm, segundos);

            String duracionTexto = formatearSegundos(segundos);
            String resumen = String.format(
                    "Distancia aproximada: %.1f km · %s estimados",
                    distanciaKm,
                    duracionTexto
            );

            return new TrafficInfo(
                    nivel,
                    0,
                    resumen,
                    distanciaKm,
                    duracionTexto,
                    "-"
            );

        } catch (Exception e) {
            e.printStackTrace();
            return new TrafficInfo(
                    "No disponible",
                    0,
                    "Servicio de ruta no accesible",
                    0.0,
                    "-",
                    "-"
            );
        }
    }

    private String calcularNivel(double distanciaKm, long duracionSegundos) {
        if (distanciaKm <= 0 || duracionSegundos <= 0) {
            return "No disponible";
        }

        double minutos = duracionSegundos / 60.0;
        double minPorKm = minutos / distanciaKm;

        if (minPorKm < 2.5) return "Bajo";
        if (minPorKm < 4.0) return "Medio";
        return "Alto";
    }

    private String formatearSegundos(long segundos) {
        long minutos = segundos / 60;
        long horas = minutos / 60;
        minutos = minutos % 60;

        if (horas > 0) {
            return horas + " h " + minutos + " min";
        }
        return minutos + " min";
    }
}