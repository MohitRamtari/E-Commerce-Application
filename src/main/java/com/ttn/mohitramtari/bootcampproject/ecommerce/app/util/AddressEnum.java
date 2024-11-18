package com.ttn.mohitramtari.bootcampproject.ecommerce.app.util;

public enum AddressEnum {
    HOME,
    OFFICE,
    OTHERS;

    public static AddressEnum findByLabel(String name) {
        AddressEnum result = null;
        for (AddressEnum label : values()) {
            if (label.name().equalsIgnoreCase(name)) {
                result = label;
                break;
            }
        }
        return result;
    }
}
