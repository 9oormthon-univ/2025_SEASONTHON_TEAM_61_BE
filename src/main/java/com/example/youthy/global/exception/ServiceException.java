package com.example.youthy.global.exception;

import com.example.youthy.global.response.ErrorCode;
import lombok.Getter;

/**
 * 서비스 로직에서 발생하는 예외를 처리하기 위한 사용자 정의 예외 클래스입니다.
 * ErrorCode를 가질 수 있게 한다.
 */
@Getter
public class ServiceException extends RuntimeException {

    private final ErrorCode errorCode;
    private String detailMessage; // 기본 메시지 외에 추가적인 상세 정보를 제공하고 싶을 때 사용

    public ServiceException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ServiceException(ErrorCode errorCode, String detailMessage) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.detailMessage = detailMessage;
    }
}
