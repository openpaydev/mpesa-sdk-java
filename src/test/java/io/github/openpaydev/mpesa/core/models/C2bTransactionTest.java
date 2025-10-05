package io.github.openpaydev.mpesa.core.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class C2bTransactionTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Should correctly deserialize a C2B transaction JSON payload")
    void shouldDeserializeFromJsonCorrectly() throws Exception {
        String c2bPayloadJson = "{\n" +
                "    \"TransactionType\": \"Pay Bill\",\n" +
                "    \"TransID\": \"RKTQ48696S\",\n" +
                "    \"TransTime\": \"20231122153000\",\n" +
                "    \"TransAmount\": \"100.00\",\n" +
                "    \"BusinessShortCode\": \"600988\",\n" +
                "    \"BillRefNumber\": \"TestACC123\",\n" +
                "    \"InvoiceNumber\": \"\",\n" +
                "    \"OrgAccountBalance\": \"50000.00\",\n" +
                "    \"ThirdPartyTransID\": \"\",\n" +
                "    \"MSISDN\": \"254708374149\",\n" +
                "    \"FirstName\": \"John\",\n" +
                "    \"MiddleName\": \"Fitz\",\n" +
                "    \"LastName\": \"Doe\"\n" +
                "}";

        C2bTransaction transaction = objectMapper.readValue(c2bPayloadJson, C2bTransaction.class);

        assertNotNull(transaction);
        assertEquals("Pay Bill", transaction.getTransactionType());
        assertEquals("RKTQ48696S", transaction.getTransactionId());
        assertEquals("20231122153000", transaction.getTransactionTime());
        assertEquals("100.00", transaction.getTransactionAmount());
        assertEquals("600988", transaction.getBusinessShortCode());
        assertEquals("TestACC123", transaction.getBillRefNumber());
        assertEquals("254708374149", transaction.getMsisdn());
        assertEquals("John", transaction.getFirstName());
        assertEquals("Fitz", transaction.getMiddleName());
        assertEquals("Doe", transaction.getLastName());
    }
}