package com.example.youthy.chungheon;

import com.example.youthy.YouthPolicy;
import com.example.youthy.YouthPolicyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
