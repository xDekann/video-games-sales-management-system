package com.pk.vgsms.model.entity.payment;

public enum Deliveries {
    COURIER("Courier"),
    SELF_PICKUP("Self Pickup"),
    PACZKOMAT("Paczkomat");
    public final String deliveryName;

    Deliveries(String deliveryName) {
        this.deliveryName = deliveryName;
    }

    public static boolean containsDelivery(String deliveryName) {
        for (Deliveries delivery : values()) {
            if (delivery.deliveryName.equals(deliveryName)) {
                return true;
            }
        }
        return false;
    }
}
