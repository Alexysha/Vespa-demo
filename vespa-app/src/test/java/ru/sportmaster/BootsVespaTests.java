package ru.sportmaster;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import ru.sportmaster.model.Availability;
import ru.sportmaster.model.Boots;
import ru.sportmaster.model.VespaDocument;
import ru.sportmaster.model.VespaDocument.Fields;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.sportmaster.model.enums.Gender.FEMALE;
import static ru.sportmaster.model.enums.Season.WINTER;

@TestMethodOrder(OrderAnnotation.class)
class BootsVespaTests {

    private final ObjectMapper objectMapper = new ObjectMapper().disable(FAIL_ON_UNKNOWN_PROPERTIES);
    private final TypeReference<VespaDocument<Boots>> TYPE_REF = new TypeReference<>() {};

    @Test
    @Order(1)
    @DisplayName("Пример сохранения документа в Vespa")
    void saveBootsTest() {
        @Cleanup val client = HttpClient.newHttpClient();
        val boots = getBoots();
        val vespaDocument = getVespaDocument(boots);
        val json = objectToJson(vespaDocument);
        val request = HttpRequest.newBuilder()
                .uri(getUri())
                .header("content-type", "application/json")
                .POST(BodyPublishers.ofString(json))
                .build();

        val response = sendRequest(client, request);
        val statusCode = response.statusCode();

        assertEquals(200, statusCode);
    }

    @Test
    @Order(2)
    @DisplayName("Пример получения документа из Vespa")
    void getBootsTest() {
        @Cleanup val client = HttpClient.newHttpClient();
        val request = HttpRequest.newBuilder()
                .uri(getUri())
                .header("content-type", "application/json")
                .GET()
                .build();

        val response = sendRequest(client, request);
        val statusCode = response.statusCode();
        val body = response.body();
        val result = jsonToObject(body);
        val expected = getVespaDocument(getBoots());

        assertEquals(200, statusCode);
        assertEquals(expected, result);
    }

    @AfterAll
    static void deleteTestData() {
        @Cleanup val client = HttpClient.newHttpClient();
        val request = HttpRequest.newBuilder()
                .uri(getUri())
                .header("content-type", "application/json")
                .DELETE()
                .build();

        val statusCode = sendRequest(client, request).statusCode();

        assertEquals(200, statusCode);
    }

    @SneakyThrows
    private static HttpResponse<String> sendRequest(HttpClient client, HttpRequest request) {
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private Boots getBoots() {
        val boots = new Boots();
        boots.setId(28001450299L);
        boots.setName("Кроссовки утепленные женские Nike City Classic");
        boots.setDescription("Готова к зимним выходам в свет? City Classic от Nike уверены, что тебе есть куда отправиться с друзьями в выходные.");
        boots.setPrice(
            new BigDecimal(17_009)
        );
        boots.setAvailabilities(
            getAvailabilities()
        );
        boots.setSeason(WINTER);
        boots.setMaterial(
            Map.of(
                "Натуральная кожа", 45,
                "Синтетическая кожа", 35,
                "Текстиль", 20
            )
        );
        boots.setGender(FEMALE);
        boots.setMoisture(false);
        boots.setZipper(true);
        return boots;
    }

    private List<Availability> getAvailabilities() {
        val availability = new Availability();
        availability.setId(2L);
        availability.setName("ТЦ Вегас");
        availability.setCount(5);
        return List.of(availability);
    }

    private VespaDocument<Boots> getVespaDocument(Boots boots) {
        val fields = new Fields<Boots>();
        fields.setDocument(boots);
        val vespaDocument = new VespaDocument<Boots>();
        vespaDocument.setFields(fields);
        return vespaDocument;
    }

    @SneakyThrows
    private String objectToJson(VespaDocument<Boots> document) {
        return objectMapper.writeValueAsString(document);
    }

    @SneakyThrows
    private VespaDocument<Boots> jsonToObject(String json) {
        return objectMapper.readValue(json, TYPE_REF);
    }

    @SneakyThrows
    private static URI getUri() {
        return new URI("http://localhost:8080/document/v1/shop/boots/docid/1");
    }
}
