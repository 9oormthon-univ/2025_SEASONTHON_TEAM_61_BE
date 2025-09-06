package com.example.youthy.wonyeong.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AgeGroup {
    TWENTIES("20대"),
    THIRTIES("30대"),
    FORTIES_PLUS("40대 이상");

    private final String displayName;

    AgeGroup(String displayName) {
        this.displayName = displayName;
    }

    @JsonValue
    public String getDisplayName() {
        return displayName;
    }

    @JsonCreator
    public static AgeGroup fromDisplayName(String value) {
        for (AgeGroup g : AgeGroup.values()) {
            if (g.displayName.equals(value)) {
                return g;
            }
        }
        throw new IllegalArgumentException("Unknown age group: " + value);
    }
}
