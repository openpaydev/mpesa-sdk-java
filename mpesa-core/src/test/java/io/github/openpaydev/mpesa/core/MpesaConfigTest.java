package io.github.openpaydev.mpesa.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class MpesaConfigTest {

    @Test
    @DisplayName("Builder should create a valid MpesaConfig object")
    void builderCreatesCorrectConfig() {
        MpesaConfig config = MpesaConfig.builder()
                .consumerKey("test_key")
                .consumerSecret("test_secret")
                .businessShortCode("12345")
                .passKey("test_passkey")
                .environment(MpesaEnvironment.SANDBOX)
                .build();

        assertEquals("test_key", config.getConsumerKey());
        assertEquals("test_secret", config.getConsumerSecret());
        assertEquals("12345", config.getBusinessShortCode());
        assertEquals("test_passkey", config.getPassKey());
        assertEquals(MpesaEnvironment.SANDBOX, config.getEnvironment());
    }

    @Test
    @DisplayName("fromEnv should load config correctly for PRODUCTION")
    void fromEnvCreatesCorrectConfigForProduction() {
        Map<String, String> fakeEnv = new HashMap<>();
        fakeEnv.put("MPESA_CONSUMER_KEY", "prod_key");
        fakeEnv.put("MPESA_CONSUMER_SECRET", "prod_secret");
        fakeEnv.put("MPESA_SHORTCODE", "54321");
        fakeEnv.put("MPESA_PASSKEY", "prod_passkey");
        fakeEnv.put("MPESA_ENVIRONMENT", "PRODUCTION");

        MpesaConfig config = MpesaConfig.fromEnv(fakeEnv::get);

        assertEquals("prod_key", config.getConsumerKey());
        assertEquals("prod_secret", config.getConsumerSecret());
        assertEquals("54321", config.getBusinessShortCode());
        assertEquals("prod_passkey", config.getPassKey());
        assertEquals(MpesaEnvironment.PRODUCTION, config.getEnvironment());
    }

    @Test
    @DisplayName("fromEnv should default to SANDBOX if environment variable is not set")
    void fromEnvDefaultsToSandbox() {
        Map<String, String> fakeEnv = new HashMap<>();
        fakeEnv.put("MPESA_CONSUMER_KEY", "some_key");
        fakeEnv.put("MPESA_CONSUMER_SECRET", "some_secret");
        fakeEnv.put("MPESA_SHORTCODE", "112233");
        fakeEnv.put("MPESA_PASSKEY", "some_passkey");

        MpesaConfig config = MpesaConfig.fromEnv(fakeEnv::get);

        assertEquals("112233", config.getBusinessShortCode());
        assertEquals(MpesaEnvironment.SANDBOX, config.getEnvironment());
    }
}