package com.jnotifier.app;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum JNotifierEnums {
    LOGIN("login"), EMAIL_VERIFY("email_verification"), FORGOT_PWD("forgot_password");

    private String type;

    JNotifierEnums(String type) {
        this.type = type;
    }

    @JsonValue
    public String getType() {
        return type;
    }

    @JsonCreator
    public static JNotifierEnums fromString(String value) {
        // 1. Handle nulls or empty strings safely
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Verification type cannot be null or empty");
        }

        // 2. Loop through all the enums (LOGIN, EMAIL_VERIFY, FORGOT_PWD)
        for (JNotifierEnums enumValue : JNotifierEnums.values()) {
            // 3. Compare the passed string against the 'type' value (e.g., "email_verification")
            if (enumValue.getType().equalsIgnoreCase(value)) {
                return enumValue;
            }
        }

        // 4. Throw an error if the string doesn't match any of our enum types
        throw new IllegalArgumentException("Unknown verification type: " + value);
    }
}
