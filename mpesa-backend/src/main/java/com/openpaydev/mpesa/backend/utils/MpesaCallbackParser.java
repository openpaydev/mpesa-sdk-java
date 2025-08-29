package com.openpaydev.mpesa.backend.utils;

import com.google.gson.Gson;
import com.openpaydev.mpesa.backend.models.StkCallback;

public class MpesaCallbackParser {

    private final Gson gson = new Gson();

    /**
     * Parses the JSON string from M-Pesa callback into an StkCallback object.
     * @param jsonCallbackData The raw JSON string from the request body.
     * @return A deserialized StkCallback object.
     */
    public StkCallback parse(String jsonCallbackData) {
        return gson.fromJson(jsonCallbackData, StkCallback.class);
    }
}