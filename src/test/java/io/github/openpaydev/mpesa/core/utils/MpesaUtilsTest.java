package io.github.openpaydev.mpesa.core.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

class MpesaUtilsTest {

    @Test
    @DisplayName("getTimestamp(ZonedDateTime) should return formatted Nairobi time")
    void getTimestamp_withFixedDate_shouldReturnCorrectFormat() {
        ZonedDateTime fixedDateTime = ZonedDateTime.parse("2025-09-21T12:34:56+03:00[Africa/Nairobi]");
        String timestamp = MpesaUtils.getTimestamp(fixedDateTime);
        assertEquals("20250921123456", timestamp);
    }

    @Test
    @DisplayName("getTimestamp() should return a string in the format yyyyMMddHHmmss")
    void getTimestamp_shouldReturnCorrectFormat() {
        String timestamp = MpesaUtils.getTimestamp();
        assertNotNull(timestamp);
        assertTrue(timestamp.matches("^\\d{14}$"), "Timestamp should be 14 digits long.");
    }

    @Test
    @DisplayName("generatePassword should return a correctly Base64 encoded string")
    void generatePassword_shouldReturnCorrectlyEncodedString() {
        String shortCode = "174379";
        String passKey = "bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919";
        String timestamp = "20251021105921";
        String expectedPassword =
                "MTc0Mzc5YmZiMjc5ZjlhYTliZGJjZjE1OGU5N2RkNzFhNDY3Y2QyZTBjODkzMDU5YjEwZjc4ZTZiNzJhZGExZWQyYzkxOTIwMjUxMDIxMTA1OTIx";
        String actualPassword = MpesaUtils.generatePassword(shortCode, passKey, timestamp);
        assertEquals(expectedPassword, actualPassword);
    }

    @DisplayName("formatPhoneNumber should correctly format various valid inputs to 254 format")
    @ParameterizedTest(name = "Input: {0} -> Expected: 254712345678")
    @CsvSource({
            "0712345678",
            "712345678",
            "254712345678",
            "+254712345678"
    })
    void formatPhoneNumber_shouldHandleValidFormats(String input) {
        String expected = "254712345678";
        String actual = MpesaUtils.formatPhoneNumber(input);
        assertEquals(expected, actual);
    }

    @DisplayName("formatPhoneNumber should throw exception for null or empty input")
    @ParameterizedTest(name = "Input: {0}")
    @NullAndEmptySource
    void formatPhoneNumber_shouldThrowForNullOrEmpty(String nullOrEmptyInput) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            MpesaUtils.formatPhoneNumber(nullOrEmptyInput);
        });
        assertEquals("Phone number cannot be null or empty.", exception.getMessage());
    }

    @DisplayName("formatPhoneNumber should throw exception for structurally invalid formats")
    @ParameterizedTest(name = "Input: \"{0}\"")
    @ValueSource(strings = {"12345", "0812345678", "abcde", "25412345", "+254799"})
    void formatPhoneNumber_shouldThrowForInvalidFormat(String invalidInput) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            MpesaUtils.formatPhoneNumber(invalidInput);
        });
        // stricter regex in MpesaUtils means "Invalid Kenyan phone number" for malformed ones
        assertTrue(
                exception.getMessage().startsWith("Invalid"),
                "Expected exception message to start with 'Invalid'"
        );
    }
}
