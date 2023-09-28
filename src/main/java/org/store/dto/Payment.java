package org.store.dto;

public enum Payment {
    BOTH("Both"),
    IS_PAID("Is paid"),
    NOT_PAID("Is not paid");

    private String value;

    Payment(String val){
        value = val;
    }

    @Override
    public String toString() {
        return value;
    }
}
