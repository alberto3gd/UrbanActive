package es.upm.dit.isst.grupo10.urbanactive.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.upm.dit.isst.grupo10.urbanactive.dto.WeatherInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

    public WeatherInfo getWeatherMadrid(String fechaActividad) {
        try {
            String datosUrl = getDatosUrl("/opendata/api/prediccion/especifica/municipio/diaria/28079");

            if (datosUrl == null) {
                return new WeatherInfo("Datos no disponibles", "-", "-", "-", null);
            }

            String rawJson = getRawJsonFromDatos(datosUrl);

            JsonNode root = objectMapper.readTree(rawJson);
            if (!root.isArray() || root.isEmpty()) {
                return new WeatherInfo("Datos no disponible", "-", "-", "-", null);
            }

            JsonNode prediccion = root.get(0).path("prediccion");
            JsonNode dias = prediccion.path("dia");

            JsonNode diaActividad = buscarDiaPorFecha(dias, fechaActividad);

            if (diaActividad == null) {
                return new WeatherInfo("Todavía no disponible", "-", "-", "-", null);
            }

            String estadoCielo = extraerPrimerValorNoVacio(
                    diaActividad.path("estadoCielo"),
                    "descripcion",
                    "No disponible"
            );

            String probPrecipitacion = extraerPrimerValorNoVacio(
                    diaActividad.path("probPrecipitacion"),
                    "value",
                    "-"
            );

            String temperaturaMax = diaActividad.path("temperatura").path("maxima").asText("-");
            String viento = extraerViento(diaActividad.path("viento"));

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

    private JsonNode buscarDiaPorFecha(JsonNode dias, String fechaActividad) {
        if (dias == null || !dias.isArray() || fechaActividad == null || fechaActividad.isBlank()) {
            return null;
        }

        try {
            DateTimeFormatter formatterEntrada = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate fechaBuscada = LocalDate.parse(fechaActividad, formatterEntrada);

            for (JsonNode dia : dias) {
                String fechaAemet = dia.path("fecha").asText();

                if (fechaAemet != null && !fechaAemet.isBlank()) {
                    String soloFecha = fechaAemet.length() >= 10 ? fechaAemet.substring(0, 10) : fechaAemet;
                    LocalDate fechaJson = LocalDate.parse(soloFecha);

                    if (fechaJson.equals(fechaBuscada)) {
                        return dia;
                    }
                }
            }
        } catch (Exception e) {
            return null;
        }

        return null;
    }

    private String extraerPrimerValorNoVacio(JsonNode arrayNode, String campo, String valorPorDefecto) {
        if (arrayNode != null && arrayNode.isArray()) {
            for (JsonNode item : arrayNode) {
                JsonNode valor = item.get(campo);
                if (valor != null && !valor.asText().isBlank()) {
                    if ("value".equals(campo)) {
                        return valor.asText() + "%";
                    }
                    return valor.asText();
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