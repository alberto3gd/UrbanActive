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
            return new TrafficInfo("No disponible", 0, "Sin ubicación", 0.0);
        }

        try {
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
                    cercanas + " incidencias cercanas",
                    0.0
            );

        } catch (Exception e) {
            return new TrafficInfo("No disponible", 0, "Error obteniendo tráfico", 0.0);
        }
    }

    public TrafficInfo calcularTraficoTrayecto(GeoPoint origen, GeoPoint destino) {
        if (origen == null || destino == null) {
            return new TrafficInfo("No disponible", 0, "Faltan ubicaciones", 0.0);
        }

        try {
            String url = "https://datos.madrid.es/egob/catalogo/202716-0-incidencias-trafico.json";

            String json = restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(String.class);

            JsonNode root = objectMapper.readTree(json);
            JsonNode incidencias = root.path("@graph");

            int incidenciasTrayecto = 0;

            for (JsonNode inc : incidencias) {
                double lat = inc.path("location").path("latitude").asDouble();
                double lon = inc.path("location").path("longitude").asDouble();

                if (estaCercaDelTrayecto(origen, destino, lat, lon, 1.0)) {
                    incidenciasTrayecto++;
                }
            }

            double distancia = distanciaKm(origen.lat(), origen.lon(), destino.lat(), destino.lon());

            String nivel;
            if (incidenciasTrayecto == 0) nivel = "Bajo";
            else if (incidenciasTrayecto <= 2) nivel = "Medio";
            else nivel = "Alto";

            String resumen = String.format(
                    "Distancia aproximada: %.1f km · %d incidencias en el trayecto",
                    distancia,
                    incidenciasTrayecto
            );

            return new TrafficInfo(nivel, incidenciasTrayecto, resumen, distancia);

        } catch (Exception e) {
            return new TrafficInfo("No disponible", 0, "Error obteniendo tráfico", 0.0);
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

    private boolean estaCercaDelTrayecto(GeoPoint origen, GeoPoint destino,
                                         double latInc, double lonInc,
                                         double umbralKm) {
        double d1 = distanciaKm(origen.lat(), origen.lon(), latInc, lonInc);
        double d2 = distanciaKm(destino.lat(), destino.lon(), latInc, lonInc);
        double trayecto = distanciaKm(origen.lat(), origen.lon(), destino.lat(), destino.lon());

        return (d1 + d2) <= (trayecto + umbralKm);
    }
}