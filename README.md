# Mpesa SDK for Java

[![Build Status](https://img.shields.io/github/actions/workflow/status/openpaydev/mpesa-java-sdk/build.yml?branch=main&style=for-the-badge)](https://github.com/openpaydev/mpesa-java-sdk/actions)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg?style=for-the-badge)](https://opensource.org/licenses/MIT)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.openpaydev/mpesa-sdk-java?style=for-the-badge)](https://search.maven.org/artifact/io.github.openpaydev/mpesa-sdk-java)

A modern, robust, and easy-to-use Java SDK for the Safaricom M-Pesa Daraja API.

This SDK provides a clean, fluent interface for M-Pesa APIs, starting with the widely used M-Pesa Express (STK Push). It is designed to be lightweight, thread-safe, and highly configurable, making it suitable for any Java or Kotlin application.

## Features

*   **Clean & Modern API:** A simple, intuitive client for all M-Pesa operations.
*   **Thread-Safe:** Safe for use in multi-threaded server environments.
*   **Automatic Authentication:** Handles OAuth token acquisition and caching automatically.
*   **Robust Error Handling:** Clear, specific exceptions for API and network errors.
*   **Immutable Models:** All data models are immutable for predictable state management.
*   **Testable:** Designed with dependency injection for easy mocking and testing.

## Installation

This SDK is published to Maven Central. You can add it to your project using Maven or Gradle.

### Maven

Add this to your `pom.xml`:
```xml
<dependency>
    <groupId>io.github.openpaydev</groupId>
    <artifactId>mpesa-sdk-java</artifactId>
    <version>1.0.0</version> <!-- Use the latest version -->
</dependency>
```
### Gradle

Add this to your `build.gradle` file:
```groovy
implementation 'io.github.openpaydev:mpesa-sdk-java:1.0.0' // Use the latest version
```
## Configuration
The SDK is configured via environment variables, which is a best practice for security. Before using the client, you must set the following:
* **MPESA_ENVIRONMENT:** The environment to use. Either SANDBOX (default) or PRODUCTION.
* **MPESA_CONSUMER_KEY:** Your app's consumer key from the Daraja portal.
* **MPESA_CONSUMER_SECRET:** Your app's consumer secret.
* **MPESA_SHORTCODE:** Your business shortcode (PayBill or Till Number).
* **MPESA_PASSKEY:** Your Lipa Na M-Pesa online passkey.
## Quick Start: Initiating an STK Push
Here is a complete example of how to configure the client and initiate a payment.
``` java
import io.github.openpaydev.mpesa.MpesaClient;
import io.github.openpaydev.mpesa.auth.MpesaTokenManager;
import io.github.openpaydev.mpesa.core.MpesaConfig;
import io.github.openpaydev.mpesa.core.models.StkPushRequest;
import io.github.openpaydev.mpesa.core.models.StkPushResponse;
import io.github.openpaydev.mpesa.core.models.StkStatusQueryResponse;
import okhttp3.OkHttpClient;

public class StkPushExample {

    public static void main(String[] args) {
        System.out.println("🚀 Starting M-Pesa STK Push Example...");

        try {
            // 1. Configure the SDK from environment variables
            MpesaConfig config = MpesaConfig.fromEnv();

            // 2. Create the necessary clients
            OkHttpClient httpClient = new OkHttpClient();
            MpesaTokenManager tokenManager = new MpesaTokenManager(config, httpClient);
            MpesaClient client = new MpesaClient(config, tokenManager, httpClient);

            // 3. Build the STK Push request
            StkPushRequest request = StkPushRequest.newPayBillRequest(
                    "1",                          // Amount
                    "254708374149",               // Customer's phone number
                    "TestOrder123",               // Account Reference
                    "Payment for awesome goods",  // Transaction Description
                    "https://mydomain.com/path"   // Your callback URL
            );

            // 4. Initiate the STK Push
            StkPushResponse response = client.stkPush(request);
            System.out.println("STK Push accepted for processing. CheckoutRequestID: " + response.getCheckoutRequestID());

            // 5. Query the status after a delay to see the result
            Thread.sleep(25000); // Wait for transaction to complete
            StkStatusQueryResponse queryResponse = client.queryStkStatus(response.getCheckoutRequestID());
            System.out.println("\n[RESULT] Final Transaction Status:");
            System.out.println("  Result Code: " + queryResponse.getResultCode());
            System.out.println("  Description: " + queryResponse.getResultDesc());

        } catch (Exception e) {
            System.err.println("\n[ERROR] An unexpected error occurred.");
            e.printStackTrace();
        }
    }
}
```

## Contributing

Contributions are welcome!  
Please read our CONTRIBUTING.md 
for details on our code of conduct and the process for submitting pull requests.
## License
This project is licensed under the MIT License - see the **LICENSE** file for details.ooooooo