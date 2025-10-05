package io.github.openpaydev.mpesa.core.models;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class C2bRegisterUrlRequestTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Should correctly serialize to a C2B URL Registration JSON payload")
    void shouldSerializeToJsonCorrectly() throws Exception {
        C2bRegisterUrlRequest request = C2bRegisterUrlRequest.builder()
                .shortCode("600988")
                .responseType(C2bResponseType.Completed)
                .confirmationUrl("https://my.app/c2b/confirm")
                .validationUrl("https://my.app/c2b/validate")
                .build();

        String jsonRequest = objectMapper.writeValueAsString(request);

        Map<String, Object> map = objectMapper.readValue(jsonRequest, new TypeReference<Map<String, Object>>() {});

        assertEquals("600988", map.get("ShortCode"));
        assertEquals("Completed", map.get("ResponseType"));
        assertEquals("https://my.app/c2b/confirm", map.get("ConfirmationURL"));
        assertEquals("https://my.app/c2b/validate", map.get("ValidationURL"));

        assertEquals(4, map.size());
    }
}