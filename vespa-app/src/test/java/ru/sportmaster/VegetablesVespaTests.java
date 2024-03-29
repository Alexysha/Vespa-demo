package ru.sportmaster;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import ru.sportmaster.model.Food.Energy;
import ru.sportmaster.model.Food.Nutritional;
import ru.sportmaster.model.Vegetables;
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

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@TestMethodOrder(OrderAnnotation.class)
class VegetablesVespaTests {

    private final ObjectMapper objectMapper = new ObjectMapper().disable(FAIL_ON_UNKNOWN_PROPERTIES);
    private final TypeReference<VespaDocument<Vegetables>> TYPE_REF = new TypeReference<>() {};

    @Test
    @Order(1)
    @DisplayName("Пример сохранения документа в Vespa")
    void saveVegetablesTest() {
        @Cleanup val client = HttpClient.newHttpClient();
        val vegetables = getVegetables();
        val vespaDocument = getVespaDocument(vegetables);
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
    void getVegetablesTest() {
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
        val expected = getVespaDocument(getVegetables());

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
        val expected = getVespaDocument(getVegetables());

        assertEquals(expected.getFields().getDocument().isGOST(), result.isGOST(), "Поле не считалось с international_technical_standards");
        assertEquals(expected.getFields().getDocument().getNutritional(), result.getNutritional(), "Поле не считалось с nutritional");
        assertNull(result.getName(), "Поле name должно быть пустым, т.к. его нет в сводке документов");
    }

    private SearchBody getSearchBody() {
        val searchBody = new SearchBody();
        searchBody.setYql("select * from vegetables where true");
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

    private Vegetables getVegetables() {
        val vegetables = new Vegetables();
        vegetables.setId(1);
        vegetables.setName("Огурец");
        vegetables.setProducer("ООО Свежие продукты");
        vegetables.setPrice(BigDecimal.valueOf(100.51));
        vegetables.setDescription("Свежие огурцы выращенные на русской земле");
        vegetables.setComposition(List.of("Огурец обыкновенный"));
        vegetables.setNutritional(getNutritional());
        vegetables.setEnergy(getEnergy());
        vegetables.setGOST(true);
        vegetables.setSeasonal(false);
        return vegetables;
    }

    private Nutritional getNutritional() {
        val nutritional = new Nutritional();
        nutritional.setSquirrels(0.65f);
        nutritional.setFats(0.1f);
        nutritional.setCarbohydrates(0.65f);
        return nutritional;
    }

    private Energy getEnergy() {
        val energy = new Energy();
        energy.setJoules(63);
        energy.setKcal(15);
        return energy;
    }

    private VespaDocument<Vegetables> getVespaDocument(Vegetables vegetables) {
        val fields = new Fields<Vegetables>();
        fields.setDocument(vegetables);
        val vespaDocument = new VespaDocument<Vegetables>();
        vespaDocument.setFields(fields);
        return vespaDocument;
    }

    @SneakyThrows
    private String objectToJson(VespaDocument<Vegetables> document) {
        return objectMapper.writeValueAsString(document);
    }

    @SneakyThrows
    private String objectToString(Object object) {
        return objectMapper.writeValueAsString(object);
    }

    @SneakyThrows
    private VespaDocument<Vegetables> jsonToObject(String json) {
        return objectMapper.readValue(json, TYPE_REF);
    }

    @SneakyThrows
    private static URI getUri() {
        return new URI("http://localhost:8080/document/v1/shop/vegetables/docid/1");
    }

    @SneakyThrows
    private static URI getSearchUri() {
        return new URI("http://localhost:8080/search/");
    }
}
