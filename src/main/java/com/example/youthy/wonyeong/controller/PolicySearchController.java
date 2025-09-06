package com.example.youthy.wonyeong.controller;

import com.example.youthy.wonyeong.domain.PolicyFilter;
import com.example.youthy.wonyeong.domain.PolicySummaryDto;
import com.example.youthy.wonyeong.service.PolicySearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wonyeong/policies")
@RequiredArgsConstructor
@Tag(name = "Wonyeong Policies", description = "카테고리/지역/대상/기관/모집현황 통합 검색")
public class PolicySearchController {

    private final PolicySearchService policySearchService;

    @Operation(
            summary = "정책 검색",
            description = """
        카테고리(다중) + 지역(시도/시군구) + 대상(키워드 OR 매칭) + 담당기관(키워드) + 모집현황(OPEN/CLOSED/ALWAYS)로 필터링합니다.
        - sido/sigungu는 Enum 이름(예: SEOUL, NOWON)으로 전달합니다.
        - zipCode 기반이 아니라, ZipcodeArea를 통해 구 단위 전체 우편번호로 확장하여 residence 조인 필터링합니다.
        """,
            requestBody = @RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = PolicyFilter.class),
                            examples = {
                                    @ExampleObject(name = "카테고리만",
                                            value = """
                        { "categories": ["일자리","교육"] }
                        """
                                    ),
                                    @ExampleObject(name = "카테고리 + 지역(서울 노원구) + 상태 OPEN",
                                            value = """
                        {
                          "categories": ["일자리"],
                          "sido": "SEOUL",
                          "sigungu": "NOWON",
                          "status": "OPEN"
                        }
                        """
                                    ),
                                    @ExampleObject(name = "담당기관/대상 키워드 포함",
                                            value = """
                        {
                          "categories": ["창업"],
                          "agency": "서울시",
                          "target": "대학생"
                        }
                        """
                                    )
                            }
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = PolicySummaryDtoPage.class)))
            }
    )
    @PostMapping("/search")
    public Page<PolicySummaryDto> search(
            @RequestBody PolicyFilter filter,
            @PageableDefault(size = 20, sort = "policyNo", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return policySearchService.search(filter, pageable);
    }

    // Swagger에서 Page<> 스키마 예쁘게 보이도록 래퍼 선언
    @Schema(name = "PolicySummaryDtoPage")
    static class PolicySummaryDtoPage extends PageImpl<PolicySummaryDto> {
        public PolicySummaryDtoPage() { super(java.util.List.of()); }
    }
}
