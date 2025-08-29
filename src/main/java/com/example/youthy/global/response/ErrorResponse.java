package com.example.youthy.global.response;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

@Getter
@Builder
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {
    private final LocalDateTime timestamp = LocalDateTime.now();
    private int status;
    private String error;
    private String code;
    private String message;

    @Builder
    public ErrorResponse(int status, String error, String code, String message) {
        this.status = status;
        this.error = error;
        this.code = code;
        this.message = message;
    }

    public static ResponseEntity<ErrorResponse> toResponseEntity(ErrorCode errorCode) {
        HttpStatus httpStatus = HttpStatus.valueOf(errorCode.getStatus());
        return ResponseEntity
                .status(httpStatus)
                .body(ErrorResponse.builder()
                        .status(errorCode.getStatus())      //  400
                        .error(httpStatus.name())           // "BAD_REQUEST"
                        .code(errorCode.name())             // "INVALID_PARAMETER"
                        .message(errorCode.getMessage())    // "잘못된 요청 데이터입니다."
                        .build());
    }
}

