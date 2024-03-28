package ru.sportmaster;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import ru.sportmaster.model.Phone;
import ru.sportmaster.model.VespaDocument;
import ru.sportmaster.model.VespaDocument.Fields;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.util.List;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(OrderAnnotation.class)
class VespaPhoneTests {

    private final ObjectMapper objectMapper = new ObjectMapper().disable(FAIL_ON_UNKNOWN_PROPERTIES);
    private final TypeReference<VespaDocument<Phone>> TYPE_REF = new TypeReference<>() {};

    @Test
    @Order(1)
    @DisplayName("Пример сохранения документа в Vespa")
    void savePhoneTest() {
        @Cleanup val client = HttpClient.newHttpClient();
        val phone = getPhone();
        val vespaDocument = getVespaDocument(phone);
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
    void getPhoneTest() {
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
        val expected = getVespaDocument(getPhone());

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

    private Phone getPhone() {
        val phone = new Phone();
        phone.setId(1);
        phone.setName("Смартфон Apple iPhone 15 128 ГБ, Dual nano SIM, розовый");
        phone.setProducer("Apple Computer, Inc");
        phone.setPrice(BigDecimal.valueOf(68570));
        phone.setDescription("Встречайте iPhone 15 - ваш идеальный спутник в мире безграничных возможностей!");
        phone.setCredit(true);
        phone.setAbroad(false);
        phone.setWeight(171);
        phone.setHeight(147.6f);
        phone.setWidth(71.6f);
        phone.setDepth(7.8f);
        phone.setColor("Розовый");
        phone.setOperation("iOS 17");
        phone.setScreen(6.1f);
        phone.setMaterial(
                List.of("алюминий", "стекло")
        );

        return phone;
    }

    private VespaDocument<Phone> getVespaDocument(Phone phone) {
        val fields = new Fields<Phone>();
        fields.setDocument(phone);
        val vespaDocument = new VespaDocument<Phone>();
        vespaDocument.setFields(fields);
        return vespaDocument;
    }

    @SneakyThrows
    private String objectToJson(VespaDocument<Phone> document) {
        return objectMapper.writeValueAsString(document);
    }

    @SneakyThrows
    private VespaDocument<Phone> jsonToObject(String json) {
        return objectMapper.readValue(json, TYPE_REF);
    }

    @SneakyThrows
    private static URI getUri() {
        return new URI("http://localhost:8080/document/v1/shop/phone/docid/1");
    }
}
