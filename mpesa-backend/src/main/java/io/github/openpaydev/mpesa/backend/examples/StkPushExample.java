package io.github.openpaydev.mpesa.backend.examples;

import io.github.openpaydev.mpesa.backend.MpesaClient;
import io.github.openpaydev.mpesa.backend.auth.MpesaTokenManager;
import io.github.openpaydev.mpesa.backend.models.StkPushRequest;
import io.github.openpaydev.mpesa.backend.models.StkPushResponse;
import io.github.openpaydev.mpesa.backend.models.StkStatusQueryResponse;
import io.github.openpaydev.mpesa.core.MpesaConfig;
import okhttp3.OkHttpClient;

/**
 * A runnable example demonstrating how to use the MpesaClient for an STK Push and Status Query.
 *
 * <p><b>Prerequisites:</b></p>
 * <p>Before running this example, you must set the following environment variables:</p>
 * <ul>
 *   <li>{@code MPESA_CONSUMER_KEY}: Your sandbox consumer key.</li>
 *   <li>{@code MPESA_CONSUMER_SECRET}: Your sandbox consumer secret.</li>
 *   <li>{@code MPESA_PASSKEY}: Your sandbox Lipa Na M-Pesa passkey.</li>
 *   <li>{@code MPESA_SHORTCODE}: The sandbox business shortcode (e.g., "174379").</li>
 * </ul>
 */
public class StkPushExample {

    public static void main(String[] args) {
        System.out.println("🚀 Starting M-Pesa STK Push Example...");

        try {
            MpesaConfig config = MpesaConfig.fromEnv();
            validateConfig(config);

            OkHttpClient httpClient = new OkHttpClient();
            MpesaTokenManager tokenManager = new MpesaTokenManager(config, httpClient);
            MpesaClient client = new MpesaClient(config, tokenManager, httpClient);

            StkPushRequest request = StkPushRequest.newPayBillRequest(
                    "1",                          // Amount
                    "254708374149",                 // TODO: Use a real sandbox phone number
                    "TestOrder123",               // Account Reference
                    "Payment for awesome goods",  // Transaction Description
                    "https://mydomain.com/path"   // TODO: MUST be a public HTTPS URL. Use ngrok for local testing.
            );

            System.out.println("\n[INFO] Sending STK Push request to " + request.getPhoneNumber() + "...");
            StkPushResponse response = client.stkPush(request);

            if (!"0".equals(response.getResponseCode())) {
                System.out.println("❌ STK Push request was rejected by Safaricom.");
                System.out.println("   Response: " + response);
                return;
            }

            System.out.println("✅ STK Push request accepted by Safaricom for processing.");
            System.out.println("   CheckoutRequestID: " + response.getCheckoutRequestID());
            System.out.println("   (Check the phone for a payment prompt...)");

            String checkoutRequestID = response.getCheckoutRequestID();
            System.out.println("\n[INFO] Waiting 25 seconds before querying transaction status...");
            Thread.sleep(25000);

            System.out.println("[INFO] Querying final status for CheckoutRequestID: " + checkoutRequestID);
            StkStatusQueryResponse queryResponse = client.queryStkStatus(checkoutRequestID);

            System.out.println("\n[RESULT] Final Transaction Status:");
            System.out.println("------------------------------------");
            System.out.println("  Result Code: " + queryResponse.getResultCode());
            System.out.println("  Description: " + queryResponse.getResultDesc());
            System.out.println("------------------------------------");

        } catch (Exception e) {
            System.err.println("\n[ERROR] An unexpected error occurred.");
            e.printStackTrace();
        }
    }

    private static void validateConfig(MpesaConfig config) {
        if (config.getConsumerKey() == null || config.getConsumerSecret() == null ||
                config.getPassKey() == null || config.getBusinessShortCode() == null) {
            throw new IllegalStateException(
                    "Configuration is incomplete. Please ensure all required environment variables are set: " +
                            "MPESA_CONSUMER_KEY, MPESA_CONSUMER_SECRET, MPESA_PASSKEY, MPESA_SHORTCODE"
            );
        }
    }
}