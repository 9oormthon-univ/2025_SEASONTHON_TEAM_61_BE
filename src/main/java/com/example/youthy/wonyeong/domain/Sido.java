package com.example.youthy.wonyeong.domain;

/**
 * 시·도 Enum (서울특별시만 사용)
 */
public enum Sido {
    SEOUL(11, "서울특별시");

    private final int code2;
    private final String nameKo;

    Sido(int code2, String nameKo) {
        this.code2 = code2;
        this.nameKo = nameKo;
    }

    public int getCode2() { return code2; }
    public String getNameKo() { return nameKo; }
}
