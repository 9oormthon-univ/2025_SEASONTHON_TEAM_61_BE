package com.example.youthy.global.exception;

import com.example.youthy.global.response.ErrorCode;
import com.example.youthy.global.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ErrorResponse> handleServiceException(ServiceException e) {
        ErrorCode errorCode = e.getErrorCode();

        // 예외 정보를 로그에 기록합니다.
        log.error("ServiceException occurred: code={}, message={}", errorCode.name(), errorCode.getMessage());

        // ErrorResponse 클래스의 정적 메서드를 사용하여 ResponseEntity를 생성하고 반환합니다.
        return ErrorResponse.toResponseEntity(errorCode);
    }
}

