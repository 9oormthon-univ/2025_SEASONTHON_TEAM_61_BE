package com.example.youthy.chungheon;

import com.example.youthy.YouthPolicy;
import com.example.youthy.YouthPolicyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PolicyUpdateService {

    private final ExternalPolicyApiClient apiClient;
    private final YouthPolicyRepository youthPolicyRepository;

    /**
     * 외부 API로부터 모든 정책 데이터를 가져와 데이터베이스를 업데이트(저장)합니다.
     * 이 메서드는 스케줄러를 통해 매일 새벽에 실행되도록 설정할 수 있습니다.
     */
    @Transactional
    @Scheduled(cron = "0 0 4 * * *") // 초 분 시 일 월 요일 > 새벽 4시에 DB 업데이트
    public void updateAllPoliciesFromApi() {
        log.info("Start updating policies from external API.");
        int pageNum = 1;
        final int pageSize = 100; // API가 허용하는 최대 사이즈
        int totalUpdatedCount = 0;

        while (true) {
            List<ExternalPolicyDto.YouthPolicyItem> fetchedItems = apiClient.fetchPolicies(pageNum, pageSize);

            if (fetchedItems.isEmpty()) {
                log.info("No more policies to fetch. Exiting loop.");
                break; // 더 이상 가져올 데이터가 없으면 중단
            }

            List<YouthPolicy> policiesToSave = fetchedItems.stream()
                    .filter(this::isPolicyActive)
                    .map(ExternalPolicyDto.YouthPolicyItem::toEntity)
                    .collect(Collectors.toList());

            youthPolicyRepository.saveAll(policiesToSave); // 가져온 데이터를 DB에 저장
            totalUpdatedCount += policiesToSave.size();
            log.info("Saved {} policies from page {}.", policiesToSave.size(), pageNum);

            pageNum++;
            break;
        }
        log.info("Finished updating policies. Total {} policies updated.", totalUpdatedCount);
    }
    /**
     * 정책이 마감되었는지 여부를 판단하는 헬퍼 메서드입니다.
     * @param item 외부 API로부터 받은 정책 아이템
     * @return 마감되지 않았으면 true, 마감되었으면 false
     */
    private boolean isPolicyActive(ExternalPolicyDto.YouthPolicyItem item) {
        String endDateStr = item.getAplyYmd(); //ex.20250805 ~ 20250822\N20250301 ~ 20251231
        endDateStr=endDateStr.trim().replace("\\N","\n");
        // 종료일 정보가 없거나 공백이면, 일단 유효한 공고로 판단합니다. (정책에 따라 변경 가능)
        if (!StringUtils.hasText(endDateStr)) {
            return true;
        }
        // 여러 기간이 줄바꿈(\n)으로 연결된 경우, 가장 마지막 기간을 사용합니다.
        if (endDateStr.contains("\n")) {
            String[] periods = endDateStr.split("\n");
            endDateStr = periods[periods.length - 1].trim();
        }
        // "~" 문자가 포함되어 있다면, 종료일을 추출.
        if (endDateStr.contains("~")) {
            endDateStr = endDateStr.split("~")[1].trim();
        }

        try {
            // 'YYYYMMDD' 형식의 문자열을 LocalDate 객체로 변환합니다.
            LocalDate endDate = LocalDate.parse(endDateStr.trim(), DateTimeFormatter.ofPattern("yyyyMMdd"));

            // 오늘 날짜를 가져옵니다.
            LocalDate today = LocalDate.now();

            // 종료일이 오늘이거나 오늘보다 이후인지 확인합니다. (즉, 아직 마감되지 않았는지)
            return !endDate.isBefore(today);
        } catch (Exception e) {
            log.warn("정책신청 마감일 파싱에러. [{}]: '{}'", item.getPlcyNo(), endDateStr);
            return true; // 날짜 형식이 이상할 경우, 일단 유효한 것으로 간주하여 DB에 저장 (추후 확인 필요)
        }
    }
}
