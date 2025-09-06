package io.github.openpaydev.mpesa.backend.models;

/**
 * Enum representing the M-Pesa transaction types for STK Push.
 */
public enum TransactionType {
    /**
     * For Paybill transactions.
     */
    CustomerPayBillOnline,

    /**
     * For Buy Goods transactions.
     */
    CustomerBuyGoodsOnline
}