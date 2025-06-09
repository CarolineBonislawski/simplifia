package org.cb.simplifia.common;

public class StringFormatter {

    public enum OrderType { CREDIT, DEBIT }

    public static String buildReason(String orderId, OrderType orderType) {
        return switch (orderType) {
            case DEBIT -> "Order created " + orderId;
            case CREDIT -> "Order cancelled " + orderId;
        };
    }
}
