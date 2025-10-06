package io.github.openpaydev.mpesa.core.utils;

import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

public final class MpesaUtils {

  private static final DateTimeFormatter TIMESTAMP_FORMATTER =
      DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
  private static final ZoneId NAIROBI_ZONE_ID = ZoneId.of("Africa/Nairobi");

  private MpesaUtils() {
    // prevent instantiation
  }

  public static String getTimestamp() {
    return getTimestamp(ZonedDateTime.now(NAIROBI_ZONE_ID));
  }

  public static String getTimestamp(ZonedDateTime dateTime) {
    return dateTime.withZoneSameInstant(NAIROBI_ZONE_ID).format(TIMESTAMP_FORMATTER);
  }

  public static String generatePassword(String shortCode, String passKey, String timestamp) {
    String strToEncode = shortCode + passKey + timestamp;
    return Base64.getEncoder().encodeToString(strToEncode.getBytes(StandardCharsets.UTF_8));
  }

  public static String formatPhoneNumber(String phoneNumber) {
    if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
      throw new IllegalArgumentException("Phone number cannot be null or empty.");
    }

    String normalizedPhoneNumber = phoneNumber.trim().replaceAll("\\s+", "");
    if (normalizedPhoneNumber.startsWith("+")) {
      normalizedPhoneNumber = normalizedPhoneNumber.substring(1);
    }

    String formatted;
    if (normalizedPhoneNumber.startsWith("07")) {
      formatted = "254" + normalizedPhoneNumber.substring(1);
    } else if (normalizedPhoneNumber.startsWith("7")) {
      formatted = "254" + normalizedPhoneNumber;
    } else if (normalizedPhoneNumber.startsWith("254")) {
      formatted = normalizedPhoneNumber;
    } else {
      throw new IllegalArgumentException("Invalid phone number format: " + phoneNumber);
    }

    if (!formatted.matches("^2547\\d{8}$")) {
      throw new IllegalArgumentException("Invalid Kenyan phone number: " + phoneNumber);
    }

    return formatted;
  }
}
