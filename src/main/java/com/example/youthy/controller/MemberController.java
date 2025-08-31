package com.example.youthy.controller;

import com.example.youthy.config.CurrentMember;
import com.example.youthy.domain.Member;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class MemberController {

    /**
     * 현재 로그인한 나의 정보 조회
     * - 고정 경로: GET /api/me
     * - 인증 실패 시 401 반환
     * - 성공 시 id, email, username만 반환(민감정보 제외)
     */
    @GetMapping("/me")
    public ResponseEntity<?> me(@CurrentMember Member member) {
        if (member == null) {
            // 인증 실패
            return ResponseEntity.status(401).body(new ErrorResponse("Unauthorized"));
        }

        // 성공 응답
        return ResponseEntity.ok(new MemberMeResponse(
                member.getId(),
                member.getEmail(),
                member.getUsername()
        ));
    }

    // --- 내부 DTO들 (별도 파일 없이 사용) ---

    public record MemberMeResponse(Long id, String email, String username) {}

    public record ErrorResponse(String error) {}
}
