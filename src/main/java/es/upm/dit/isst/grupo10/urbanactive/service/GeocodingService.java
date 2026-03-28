package es.upm.dit.isst.grupo10.urbanactive.service;

import es.upm.dit.isst.grupo10.urbanactive.dto.GeoPoint;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class GeocodingService {

    private final RestClient restClient;

    public GeocodingService(RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("https://nominatim.openstreetmap.org")
                .defaultHeader("User-Agent", "UrbanActive/1.0 (urbanactive@upm.es)")
                .build();
    }

    public GeoPoint buscar(String texto) {
        try {
            List<Map<String, Object>> respuesta = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/search")
                            .queryParam("q", texto)
                            .queryParam("format", "jsonv2")
                            .queryParam("limit", 1)
                            .queryParam("accept-language", "es")
                            .build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});

            if (respuesta == null || respuesta.isEmpty()) {
                return null;
            }

            Map<String, Object> item = respuesta.get(0);

            return new GeoPoint(
                    Double.parseDouble(item.get("lat").toString()),
                    Double.parseDouble(item.get("lon").toString()),
                    item.get("display_name").toString()
            );
        } catch (Exception e) {
            return null;
        }
    }
}