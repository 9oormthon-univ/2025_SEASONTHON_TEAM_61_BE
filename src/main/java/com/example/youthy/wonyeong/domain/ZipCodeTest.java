package com.example.youthy.wonyeong.domain;

public class ZipCodeTest {
    public static void main(String[] args) {
        String zip = "04156";
        Sigungu sigungu = ZipCodeMapper.findByZipCode(zip);

        if (sigungu != null) {
            System.out.println(zip + " → " + sigungu.getSido().getNameKo() + " " + sigungu.getNameKo());
        } else {
            System.out.println("해당 우편번호는 매핑되지 않았습니다.");
        }
    }
}
