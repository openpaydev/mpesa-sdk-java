package com.mpesa.backend.examples;

import com.mpesa.backend.MpesaClient;
import com.mpesa.backend.auth.MpesaTokenManager;
import com.mpesa.backend.models.StkPushRequest;
import com.mpesa.backend.models.StkPushResponse;
import com.mpesa.backend.models.StkStatusQueryResponse; // Make sure this import is added
import com.mpesa.backend.models.TransactionType;
import com.mpesa.core.MpesaConfig;
import com.mpesa.core.MpesaEnvironment;
import okhttp3.OkHttpClient;

public class StkPushTest {
    public static void main(String[] args) {
        // 1. CONFIGURE YOUR SANDBOX CREDENTIALS
        // Ensure you have set these environment variables in your run configuration or system
        MpesaConfig config = new MpesaConfig(
                System.getenv("MPESA_CONSUMER_KEY"),
                System.getenv("MPESA_CONSUMER_SECRET"),
                "174379", // This is the public sandbox shortcode
                System.getenv("MPESA_PASSKEY"),
                MpesaEnvironment.SANDBOX
        );

        MpesaTokenManager tokenManager = new MpesaTokenManager(config);
        OkHttpClient httpClient = new OkHttpClient();
        MpesaClient client = new MpesaClient(config, tokenManager, httpClient);

        // 2. SETUP THE STK PUSH REQUEST
        StkPushRequest stkPushRequest = StkPushRequest.builder()
                .phoneNumber("254722000000")
                .amount("1")
                .accountReference("OrderTest123")
                .transactionDesc("Payment for goods")
                // MUST BE a publicly accessible HTTPS URL. Use ngrok for local testing.
                .callbackUrl("https://338c295c0c93.ngrok-free.app")
                .transactionType(TransactionType.CustomerPayBillOnline)
                .build();

        try {
            // 3. INITIATE THE STK PUSH
            System.out.println("Sending STK Push request...");
            StkPushResponse response = client.stkPush(stkPushRequest);
            System.out.println("Response received:");
            System.out.println(response);

            // 4. QUERY THE TRANSACTION STATUS
            // The initial response code '0' means the request was accepted for processing.
            if ("0".equals(response.getResponseCode())) {
                String checkoutRequestID = response.getCheckoutRequestID();
                System.out.println("\n✅ STK Push accepted by Safaricom.");
                System.out.println("   Waiting 20 seconds before querying status...");

                // Wait for the user to enter their PIN.
                Thread.sleep(20000);

                System.out.println("   Querying final status for CheckoutRequestID: " + checkoutRequestID);
                StkStatusQueryResponse queryResponse = client.queryStkStatus(checkoutRequestID);

                System.out.println("\nFinal Query Response:");
                System.out.println(queryResponse);

                System.out.println("\n------------------------------------");
                System.out.println("Result Description: " + queryResponse.getResultDesc());
                System.out.println("------------------------------------");

            } else {
                System.out.println("\n❌ STK Push was rejected by Safaricom.");
                System.out.println("   Response Description: " + response.getResponseDescription());
            }

        } catch (Exception e) {
            System.err.println("\nAn error occurred:");
            e.printStackTrace();
        }
    }
}