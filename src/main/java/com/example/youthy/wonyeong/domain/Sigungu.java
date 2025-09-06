package com.example.youthy.wonyeong.domain;

import java.util.Arrays;

public enum Sigungu {
    JONGNO(Sido.SEOUL, 11110, "종로구"),
    JUNG(Sido.SEOUL, 11140, "중구"),
    YONGSAN(Sido.SEOUL, 11170, "용산구"),
    SEONGDONG(Sido.SEOUL, 11200, "성동구"),
    GWANGJIN(Sido.SEOUL, 11215, "광진구"),
    DONGDAEMUN(Sido.SEOUL, 11230, "동대문구"),
    JUNGNANG(Sido.SEOUL, 11260, "중랑구"),
    SEONGBUK(Sido.SEOUL, 11290, "성북구"),
    GANGBUK(Sido.SEOUL, 11305, "강북구"),
    DOBONG(Sido.SEOUL, 11320, "도봉구"),
    NOWON(Sido.SEOUL, 11350, "노원구"),
    EUNPYEONG(Sido.SEOUL, 11380, "은평구"),
    SEODAEMUN(Sido.SEOUL, 11410, "서대문구"),
    MAPO(Sido.SEOUL, 11440, "마포구"),
    YANGCHEON(Sido.SEOUL, 11470, "양천구"),
    GANGSEO(Sido.SEOUL, 11500, "강서구"),
    GURO(Sido.SEOUL, 11530, "구로구"),
    GEUMCHEON(Sido.SEOUL, 11545, "금천구"),
    YEONGDEUNGPO(Sido.SEOUL, 11560, "영등포구"),
    DONGJAK(Sido.SEOUL, 11590, "동작구"),
    GWANAK(Sido.SEOUL, 11620, "관악구"),
    SEOCHO(Sido.SEOUL, 11650, "서초구"),
    GANGNAM(Sido.SEOUL, 11680, "강남구"),
    SONGPA(Sido.SEOUL, 11710, "송파구"),
    GANGDONG(Sido.SEOUL, 11740, "강동구");

    private final Sido sido;
    private final int code5;
    private final String nameKo;

    Sigungu(Sido sido, int code5, String nameKo) {
        this.sido = sido;
        this.code5 = code5;
        this.nameKo = nameKo;
    }

    public Sido getSido() { return sido; }
    public int getCode5() { return code5; }
    public String getNameKo() { return nameKo; }

    // ✅ 한글 구 이름으로 Enum 찾기
    public static Sigungu fromName(String nameKo) {
        return Arrays.stream(values())
                .filter(sg -> sg.nameKo.equals(nameKo))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown sigungu: " + nameKo));
    }
}
