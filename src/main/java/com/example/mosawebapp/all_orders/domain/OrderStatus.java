package com.example.mosawebapp.all_orders.domain;

import java.util.Arrays;
import java.util.List;

public enum OrderStatus {
    NOT_YET_ORDERED("Not Yet Ordered"),
    FOR_VERIFICATION("For Verification"),
    VERIFIED("Verified"),
    INVALID_REFERENCE_NUMBER("Invalid Reference Number"),
    FOR_PICKUP("For Pickup");

    String label;

    OrderStatus(String label){ this.label = label; }

    public static List<OrderStatus> getOrderStatuses(){
        return Arrays.asList(NOT_YET_ORDERED, FOR_VERIFICATION, VERIFIED, INVALID_REFERENCE_NUMBER, FOR_PICKUP);
    }
}
