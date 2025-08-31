package com.example.youthy.exception;

import com.example.youthy.dto.ApiError;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<ApiError> build(HttpStatus status, String message, HttpServletRequest req) {
        return ResponseEntity.status(status).body(
                new ApiError(Instant.now(), status.value(), status.getReasonPhrase(), message, req.getRequestURI()));
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<ApiError> badRequest(RuntimeException e, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, e.getMessage(), req);
    }

    @ExceptionHandler({MissingServletRequestParameterException.class, MethodArgumentNotValidException.class})
    public ResponseEntity<ApiError> badParams(Exception e, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, e.getMessage(), req);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ApiError> tokenExpired(ExpiredJwtException e, HttpServletRequest req) {
        return build(HttpStatus.UNAUTHORIZED, "Token expired", req);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiError> tokenInvalid(JwtException e, HttpServletRequest req) {
        return build(HttpStatus.UNAUTHORIZED, "Invalid token", req);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> unknown(Exception e, HttpServletRequest req) {
        // 개발 중에는 메시지 노출, 운영에서는 로깅 후 일반화
        return build(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), req);
    }
}
