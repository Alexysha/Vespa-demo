package ru.sportmaster;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import ru.sportmaster.model.Availability;
import ru.sportmaster.model.Sneakers;
import ru.sportmaster.model.VespaDocument;
import ru.sportmaster.model.VespaDocument.Fields;
import ru.sportmaster.search.SearchBody;
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
import static org.junit.jupiter.api.Assertions.assertNull;
import static ru.sportmaster.model.enums.Gender.MALE;
import static ru.sportmaster.model.enums.Pavement.ASPHALT;
import static ru.sportmaster.model.enums.Season.SUMMER;
import static ru.sportmaster.model.enums.Sport.RUNNING;

@TestMethodOrder(OrderAnnotation.class)
class SneakersVespaTests {

    private final ObjectMapper objectMapper = new ObjectMapper().disable(FAIL_ON_UNKNOWN_PROPERTIES);
    private final TypeReference<VespaDocument<Sneakers>> TYPE_REF = new TypeReference<>() {};

    @Test
    @Order(1)
    @DisplayName("Пример сохранения документа в Vespa")
    void saveSneakersTest() {
        @Cleanup val client = HttpClient.newHttpClient();
        val sneakers = getSneakers();
        val vespaDocument = getVespaDocument(sneakers);
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
    void getSneakersTest() {
        @Cleanup val client = HttpClient.newHttpClient();
        val request = HttpRequest.newBuilder()
                .uri(getUri())
                .header("content-type", "application/json")
                .GET()
                .build();

        val response = sendRequest(client, request);
        val statusCode = response.statusCode();

        assertEquals(200, statusCode);

        val body = response.body();
        val result = jsonToObject(body);
        val expected = getVespaDocument(getSneakers());

        assertEquals(expected, result);
    }

    @Test
    @Order(3)
    @DisplayName("Пример работы сводки документа: demo-summary")
    void summaryTest() {
        @Cleanup val client = HttpClient.newHttpClient();
        val searchBody = getSearchBody();
        val json = objectToString(searchBody);
        val request = HttpRequest.newBuilder()
                .uri(getSearchUri())
                .header("content-type", "application/json")
                .POST(BodyPublishers.ofString(json))
                .build();

        val response = sendRequest(client, request);
        val statusCode = response.statusCode();

        assertEquals(200, statusCode);

        val body = response.body();
        val result = jsonToObject(body).getRoot().getChildren().getFirst().getFields().getDocument();
        val expected = getVespaDocument(getSneakers());

        assertEquals(expected.getFields().getDocument().getPavement(), result.getPavement(), "Поле не считалось с pavement_demo_rename");
        assertNull(result.getName(), "Поле name должно быть пустым, т.к. его нет в сводке документов");
    }

    private SearchBody getSearchBody() {
        val searchBody = new SearchBody();
        searchBody.setYql("select * from sneakers where true");
        searchBody.setPresentation("demo-summary");
        return searchBody;
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

    private Sneakers getSneakers() {
        val sneakers = new Sneakers();
        sneakers.setId(26261760299L);
        sneakers.setName("Кроссовки мужские Nike Revolution 6");
        sneakers.setDescription("Комфортные кроссовки Nike Revolution 6 — то что нужно для ежедневных пробежек.");
        sneakers.setPrice(
            new BigDecimal(9_239)
        );
        sneakers.setAvailabilities(
            getAvailabilities()
        );
        sneakers.setSeason(SUMMER);
        sneakers.setMaterial(
            Map.of(
                "Текстиль", 90,
                "Термополиуретан", 10
            )
        );
        sneakers.setGender(MALE);
        sneakers.setSport(RUNNING);
        sneakers.setPavement(ASPHALT);
        sneakers.setInsole(false);
        return sneakers;
    }

    private List<Availability> getAvailabilities() {
        val availability = new Availability();
        availability.setId(1L);
        availability.setName("ТЦ Щелковский");
        availability.setCount(3);
        return List.of(availability);
    }

    private VespaDocument<Sneakers> getVespaDocument(Sneakers sneakers) {
        val fields = new Fields<Sneakers>();
        fields.setDocument(sneakers);
        val vespaDocument = new VespaDocument<Sneakers>();
        vespaDocument.setFields(fields);
        return vespaDocument;
    }

    @SneakyThrows
    private String objectToJson(VespaDocument<Sneakers> document) {
        return objectMapper.writeValueAsString(document);
    }

    @SneakyThrows
    private String objectToString(Object object) {
        return objectMapper.writeValueAsString(object);
    }

    @SneakyThrows
    private VespaDocument<Sneakers> jsonToObject(String json) {
        return objectMapper.readValue(json, TYPE_REF);
    }

    @SneakyThrows
    private static URI getUri() {
        return new URI("http://localhost:8080/document/v1/shop/sneakers/docid/1");
    }

    @SneakyThrows
    private static URI getSearchUri() {
        return new URI("http://localhost:8080/search/");
    }
}
