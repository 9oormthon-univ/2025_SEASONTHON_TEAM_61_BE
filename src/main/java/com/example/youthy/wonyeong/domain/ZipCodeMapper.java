package com.example.youthy.wonyeong.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * 우편번호(5자리) → Sigungu 매핑 (서울 25개 구 대표 우편번호 포함)
 */
public class ZipCodeMapper {
    private static final Map<String, Sigungu> ZIP_TO_SIGUNGU = new HashMap<>();

    static {
        // 종로구 (대표: 03000)
        ZIP_TO_SIGUNGU.put("03000", Sigungu.JONGNO);
        ZIP_TO_SIGUNGU.put("03100", Sigungu.JONGNO);

        // 중구 (대표: 04500)
        ZIP_TO_SIGUNGU.put("04500", Sigungu.JUNG);
        ZIP_TO_SIGUNGU.put("04600", Sigungu.JUNG);

        // 용산구 (대표: 04300)
        ZIP_TO_SIGUNGU.put("04300", Sigungu.YONGSAN);
        ZIP_TO_SIGUNGU.put("04400", Sigungu.YONGSAN);

        // 성동구 (대표: 04700)
        ZIP_TO_SIGUNGU.put("04700", Sigungu.SEONGDONG);
        ZIP_TO_SIGUNGU.put("04800", Sigungu.SEONGDONG);

        // 광진구 (대표: 05000)
        ZIP_TO_SIGUNGU.put("05000", Sigungu.GWANGJIN);
        ZIP_TO_SIGUNGU.put("05100", Sigungu.GWANGJIN);

        // 동대문구 (대표: 02500)
        ZIP_TO_SIGUNGU.put("02500", Sigungu.DONGDAEMUN);
        ZIP_TO_SIGUNGU.put("02600", Sigungu.DONGDAEMUN);

        // 중랑구 (대표: 02000)
        ZIP_TO_SIGUNGU.put("02000", Sigungu.JUNGNANG);
        ZIP_TO_SIGUNGU.put("02100", Sigungu.JUNGNANG);

        // 성북구 (대표: 02800)
        ZIP_TO_SIGUNGU.put("02800", Sigungu.SEONGBUK);
        ZIP_TO_SIGUNGU.put("02900", Sigungu.SEONGBUK);

        // 강북구 (대표: 01000)
        ZIP_TO_SIGUNGU.put("01000", Sigungu.GANGBUK);
        ZIP_TO_SIGUNGU.put("01100", Sigungu.GANGBUK);

        // 도봉구 (대표: 01300)
        ZIP_TO_SIGUNGU.put("01300", Sigungu.DOBONG);
        ZIP_TO_SIGUNGU.put("01400", Sigungu.DOBONG);

        // 노원구 (대표: 01600)
        ZIP_TO_SIGUNGU.put("01600", Sigungu.NOWON);
        ZIP_TO_SIGUNGU.put("01700", Sigungu.NOWON);

        // 은평구 (대표: 03300)
        ZIP_TO_SIGUNGU.put("03300", Sigungu.EUNPYEONG);
        ZIP_TO_SIGUNGU.put("03400", Sigungu.EUNPYEONG);

        // 서대문구 (대표: 03700)
        ZIP_TO_SIGUNGU.put("03700", Sigungu.SEODAEMUN);
        ZIP_TO_SIGUNGU.put("03800", Sigungu.SEODAEMUN);

        // 마포구 (대표: 04100)
        ZIP_TO_SIGUNGU.put("04100", Sigungu.MAPO);
        ZIP_TO_SIGUNGU.put("04200", Sigungu.MAPO);

        // 양천구 (대표: 07900)
        ZIP_TO_SIGUNGU.put("07900", Sigungu.YANGCHEON);
        ZIP_TO_SIGUNGU.put("08000", Sigungu.YANGCHEON);

        // 강서구 (대표: 07500)
        ZIP_TO_SIGUNGU.put("07500", Sigungu.GANGSEO);
        ZIP_TO_SIGUNGU.put("07600", Sigungu.GANGSEO);

        // 구로구 (대표: 08200)
        ZIP_TO_SIGUNGU.put("08200", Sigungu.GURO);
        ZIP_TO_SIGUNGU.put("08300", Sigungu.GURO);

        // 금천구 (대표: 08500)
        ZIP_TO_SIGUNGU.put("08500", Sigungu.GEUMCHEON);
        ZIP_TO_SIGUNGU.put("08600", Sigungu.GEUMCHEON);

        // 영등포구 (대표: 07200)
        ZIP_TO_SIGUNGU.put("07200", Sigungu.YEONGDEUNGPO);
        ZIP_TO_SIGUNGU.put("07300", Sigungu.YEONGDEUNGPO);

        // 동작구 (대표: 06900)
        ZIP_TO_SIGUNGU.put("06900", Sigungu.DONGJAK);
        ZIP_TO_SIGUNGU.put("07000", Sigungu.DONGJAK);

        // 관악구 (대표: 08700)
        ZIP_TO_SIGUNGU.put("08700", Sigungu.GWANAK);
        ZIP_TO_SIGUNGU.put("08800", Sigungu.GWANAK);

        // 서초구 (대표: 06500)
        ZIP_TO_SIGUNGU.put("06500", Sigungu.SEOCHO);
        ZIP_TO_SIGUNGU.put("06600", Sigungu.SEOCHO);

        // 강남구 (대표: 06000)
        ZIP_TO_SIGUNGU.put("06000", Sigungu.GANGNAM);
        ZIP_TO_SIGUNGU.put("06100", Sigungu.GANGNAM);

        // 송파구 (대표: 05700)
        ZIP_TO_SIGUNGU.put("05700", Sigungu.SONGPA);
        ZIP_TO_SIGUNGU.put("05800", Sigungu.SONGPA);

        // 강동구 (대표: 05200)
        ZIP_TO_SIGUNGU.put("05200", Sigungu.GANGDONG);
        ZIP_TO_SIGUNGU.put("05300", Sigungu.GANGDONG);
    }

    public static Sigungu findByZipCode(String zipCode) {
        return ZIP_TO_SIGUNGU.get(zipCode);
    }
}
