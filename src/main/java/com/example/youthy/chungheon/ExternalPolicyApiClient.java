package com.example.youthy.chungheon;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class ExternalPolicyApiClient {

    private final RestTemplate restTemplate;
    private final String apiUrl;
    private final String apiKey;

    public ExternalPolicyApiClient(RestTemplate restTemplate,
                                   @Value("${youth-center.api.url}") String apiUrl,
                                   @Value("${youth-center.api.key}") String apiKey) {
        this.restTemplate = restTemplate;
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
    }

    /**
     * 특정 페이지의 정책 목록을 외부 API를 통해 가져옵니다.
     * @param pageNum 페이지 번호
     * @param pageSize 페이지 사이즈
     * @return 정책 아이템 DTO 리스트
     */

    //외부 데이터를 우리 DB로
    public List<ExternalPolicyDto.YouthPolicyItem> fetchPolicies(int pageNum, int pageSize) {
        URI uri = UriComponentsBuilder
                .fromUriString(apiUrl)
                .queryParam("apiKeyNm", apiKey)
                .queryParam("rtnType", "json")
                .queryParam("pageNum", pageNum)
                .queryParam("pageSize", pageSize)
                .build(true)
                .toUri();

        try {
            ExternalPolicyDto.YouthPolicyApiResponse response = restTemplate.getForObject(uri, ExternalPolicyDto.YouthPolicyApiResponse.class);
            return Optional.ofNullable(response)
                    .map(ExternalPolicyDto.YouthPolicyApiResponse::getYouthPolicyList)
                    .orElse(Collections.emptyList());
        } catch (Exception e) { //글로벌 예외처리 필요
            log.error("Failed to fetch policies from external API for page {}: {}", pageNum, e.getMessage());
            return Collections.emptyList(); // 에러 발생 시 빈 리스트 반환
        }
    }
}
