package com.example.youthy.wonyeong.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Category {
    JOB("취업"),
    STARTUP("창업"),
    HOUSING("주거"),
    EDUCATION("교육"),
    WELFARE("복지"),
    CULTURE("문화/예술"),
    RIGHTS("참여권리"),
    ETC("기타");

    private final String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }

    @JsonValue
    public String getDisplayName() {
        return displayName;
    }

    @JsonCreator
    public static Category fromDisplayName(String value) {
        for (Category c : Category.values()) {
            if (c.displayName.equals(value)) {
                return c;
            }
        }
        throw new IllegalArgumentException("Unknown category: " + value);
    }
}
