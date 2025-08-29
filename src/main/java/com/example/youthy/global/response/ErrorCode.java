package com.example.youthy.global.response;

import lombok.Getter;

@Getter
public enum ErrorCode {

    // 400 Bad Request
    INVALID_PARAMETER(400, "잘못된 요청 데이터입니다."),
    INVALID_FILE_PATH(400, "잘못된 파일 경로 입니다."),
    INVALID_CHECK(400, "해당 값이 유효하지 않습니다."),
    INVALID_AUTHENTICATION(400, "잘못된 인증입니다."),
    INVALID_INPUT_VALUE(400, "잘못된 입력값 입니다."),

    // 401 Unauthorized
    UNAUTHORIZED(401, "인증 정보가 유효하지 않습니다."),
    EXPIRED_TOKEN(401, "토큰이 만료되었습니다."),
    INVALID_TOKEN(401, "유효하지 않은 토큰입니다."),

    // 403 Forbidden
    FORBIDDEN(403, "해당 요청에 대한 접근 권한이 없습니다."),
    INSUFFICIENT_PERMISSIONS(403, "권한이 부족합니다."),

    // 404 Not Found
    NOT_FOUND(404, "요청한 리소스를 찾을 수 없습니다."),
    USER_NOT_FOUND(404, "사용자를 찾을 수 없습니다."),
    POST_NOT_FOUND(404, "게시물을 찾을 수 없습니다."),

    // 409 Conflict
    DUPLICATE_RESOURCE(409, "이미 존재하는 리소스입니다."),
    DUPLICATE_USER_ID(409, "이미 사용 중인 아이디입니다."),

    // 500 Internal Server Error
    INTERNAL_SERVER_ERROR(500, "서버 내부 오류가 발생했습니다.");

    private final int status;
    private final String message;

    ErrorCode(final int status, final String message) {
        this.status = status;
        this.message = message;
    }
}