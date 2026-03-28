package es.upm.dit.isst.grupo10.urbanactive.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.upm.dit.isst.grupo10.urbanactive.dto.WeatherInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class AemetService {

    @Value("${aemet.api-key}")
    private String apiKey;

    private final RestClient restClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AemetService(RestClient.Builder builder) {
        this.restClient = builder.baseUrl("https://opendata.aemet.es").build();
    }

    public String getDatosUrl(String path) {
        Map<String, Object> wrapper = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(path)
                        .queryParam("api_key", apiKey)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        if (wrapper == null || wrapper.get("datos") == null) {
            return null;
        }
        return wrapper.get("datos").toString();
    }

    public String getRawJsonFromDatos(String datosUrl) {
        return RestClient.create().get()
                .uri(datosUrl)
                .retrieve()
                .body(String.class);
    }

    public WeatherInfo getWeatherMadrid() {
        try {
            String datosUrl = getDatosUrl("/opendata/api/prediccion/especifica/municipio/diaria/28079");

            if (datosUrl == null) {
                return new WeatherInfo("No disponible", "-", "-", "-", null);
            }

            String rawJson = getRawJsonFromDatos(datosUrl);

            JsonNode root = objectMapper.readTree(rawJson);
            if (!root.isArray() || root.isEmpty()) {
                return new WeatherInfo("No disponible", "-", "-", "-", null);
            }

            JsonNode prediccion = root.get(0).path("prediccion");
            JsonNode diaHoy = prediccion.path("dia").get(0);

            String estadoCielo = extraerPrimerValorNoVacio(diaHoy.path("estadoCielo"), "descripcion", "No disponible");
            String probPrecipitacion = extraerPrimerValorNoVacio(diaHoy.path("probPrecipitacion"), "value", "-");
            String temperaturaMax = diaHoy.path("temperatura").path("maxima").asText("-");
            String viento = extraerViento(diaHoy.path("viento"));

            String temperaturaTexto = temperaturaMax.equals("-") ? "-" : temperaturaMax + "ºC";

            return new WeatherInfo(
                    estadoCielo,
                    temperaturaTexto,
                    viento,
                    probPrecipitacion,
                    null
            );

        } catch (Exception e) {
            return new WeatherInfo("No disponible", "-", "-", "-", null);
        }
    }

    private String extraerPrimerValorNoVacio(JsonNode arrayNode, String campo, String valorPorDefecto) {
        if (arrayNode != null && arrayNode.isArray()) {
            for (JsonNode item : arrayNode) {
                JsonNode valor = item.get(campo);
                if (valor != null && !valor.asText().isBlank()) {
                    return valor.asText();
                }

                if ("value".equals(campo)) {
                    JsonNode alt = item.get("value");
                    if (alt != null && !alt.asText().isBlank()) {
                        return alt.asText() + "%";
                    }
                }
            }
        }
        return valorPorDefecto;
    }

    private String extraerViento(JsonNode vientoArray) {
        if (vientoArray != null && vientoArray.isArray()) {
            for (JsonNode item : vientoArray) {
                String velocidad = item.path("velocidad").asText("");
                String direccion = item.path("direccion").asText("");

                if (!velocidad.isBlank()) {
                    if (!direccion.isBlank()) {
                        return velocidad + " km/h " + direccion;
                    }
                    return velocidad + " km/h";
                }
            }
        }
        return "-";
    }
}