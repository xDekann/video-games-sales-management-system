package com.pk.vgsms.model.entity.payment;

import java.util.Arrays;
import java.util.List;

public enum Payments {

    CARD("Card"),
    PAYPAL("Paypal");

    public final String paymentName;

    Payments(String paymentName) {
        this.paymentName = paymentName;
    }

    public static boolean containsPayment(String paymentName) {
        for (Payments payment : values()) {
            if (payment.paymentName.equals(paymentName)) {
                return true;
            }
        }
        return false;
    }
}
