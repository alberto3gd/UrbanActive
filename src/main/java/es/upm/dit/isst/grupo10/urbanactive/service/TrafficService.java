package es.upm.dit.isst.grupo10.urbanactive.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.upm.dit.isst.grupo10.urbanactive.dto.GeoPoint;
import es.upm.dit.isst.grupo10.urbanactive.dto.TrafficInfo;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class TrafficService {

    private final RestClient restClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TrafficService() {
        this.restClient = RestClient.create();
    }

    public TrafficInfo calcularNivelSimple(GeoPoint punto) {

        if (punto == null) {
            return new TrafficInfo("No disponible", 0, "Sin ubicación");
        }

        try {
            // Dataset público de incidencias Madrid (JSON)
            String url = "https://datos.madrid.es/egob/catalogo/202716-0-incidencias-trafico.json";

            String json = restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(String.class);

            JsonNode root = objectMapper.readTree(json);

            JsonNode incidencias = root.path("@graph");

            int cercanas = 0;

            for (JsonNode inc : incidencias) {

                double lat = inc.path("location").path("latitude").asDouble();
                double lon = inc.path("location").path("longitude").asDouble();

                double distancia = distanciaKm(
                        punto.lat(), punto.lon(),
                        lat, lon
                );

                // radio de 2 km
                if (distancia < 2) {
                    cercanas++;
                }
            }

            String nivel;
            if (cercanas == 0) nivel = "Bajo";
            else if (cercanas == 1) nivel = "Medio";
            else nivel = "Alto";

            return new TrafficInfo(
                    nivel,
                    cercanas,
                    cercanas + " incidencias cercanas"
            );

        } catch (Exception e) {
            return new TrafficInfo("No disponible", 0, "Error obteniendo tráfico");
        }
    }

    private double distanciaKm(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a =
                Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) *
                Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2);

        return 2 * R * Math.asin(Math.sqrt(a));
    }
}