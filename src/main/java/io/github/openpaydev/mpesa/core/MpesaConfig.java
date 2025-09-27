package io.github.openpaydev.mpesa.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.function.Function;

/**
 * Configuration object for the Mpesa API client.
 * <p>
 * This class holds all the necessary credentials and settings required to interact with the Mpesa API.
 * It is immutable and should be created once and reused throughout your application.
 * <p>
 * An instance can be created using the builder pattern or from environment variables.
 * <p>
 * Example using the builder:
 * <pre>{@code
 * MpesaConfig config = MpesaConfig.builder()
 *         .consumerKey("your_key")
 *         .consumerSecret("your_secret")
 *         .businessShortCode("174379")
 *         .passKey("your_passkey")
 *         .environment(MpesaEnvironment.SANDBOX)
 *         .build();
 * }</pre>
 */
@Getter
@Builder
@AllArgsConstructor
public class MpesaConfig {

    /**
     * The Consumer Key obtained from the Safaricom Developer Portal.
     */
    private final String consumerKey;

    /**
     * The Consumer Secret obtained from the Safaricom Developer Portal.
     */
    private final String consumerSecret;

    /**
     * The Business Short Code, which is the identifier for your PayBill or Till Number.
     * Renamed from 'shortCode' to align with the official M-Pesa API documentation.
     */
    private final String businessShortCode;

    /**
     * The Lipa Na M-Pesa Pass Key, used to encrypt the password for STK Push requests.
     */
    private final String passKey;

    /**
     * The Mpesa environment to use, either {@link MpesaEnvironment#SANDBOX} or {@link MpesaEnvironment#PRODUCTION}.
     */
    private final MpesaEnvironment environment;

    /**
     * Creates a {@link MpesaConfig} instance by reading credentials from environment variables.
     * <p>
     * This is the recommended way to configure the SDK in a production environment.
     * It uses {@code System.getenv()} as the source for the variables.
     *
     * @return A new {@link MpesaConfig} instance.
     */
    public static MpesaConfig fromEnv() {
        return fromEnv(System::getenv);
    }

    /**
     * Creates a {@link MpesaConfig} instance using a provided function to look up environment variables.
     * <p>
     * This method is designed for testability, allowing you to inject a fake environment.
     *
     * @param envProvider A function that takes an environment variable name (String) and returns its value (String).
     * @return A new {@link MpesaConfig} instance.
     */
    static MpesaConfig fromEnv(Function<String, String> envProvider) {
        String mpesaEnvironment = envProvider.apply("MPESA_ENVIRONMENT");

        return MpesaConfig.builder()
                .consumerKey(envProvider.apply("MPESA_CONSUMER_KEY"))
                .consumerSecret(envProvider.apply("MPESA_CONSUMER_SECRET"))
                .businessShortCode(envProvider.apply("MPESA_SHORTCODE"))
                .passKey(envProvider.apply("MPESA_PASSKEY"))
                .environment(
                        "PRODUCTION".equalsIgnoreCase(mpesaEnvironment)
                                ? MpesaEnvironment.PRODUCTION
                                : MpesaEnvironment.SANDBOX
                )
                .build();
    }
}