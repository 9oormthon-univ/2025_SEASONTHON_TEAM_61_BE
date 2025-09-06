package com.example.youthy.wonyeong3.scrap.service;

import com.example.youthy.domain.Member;
import com.example.youthy.wonyeong3.scrap.domain.Scrap;
import com.example.youthy.wonyeong3.scrap.repository.ScrapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScrapService {

    private final ScrapRepository scrapRepository;

    @Transactional
    public void add(Member member, String policyNo) {
        if (scrapRepository.existsByMemberAndPolicyNo(member, policyNo)) {
            // 이미 존재 → 409로 올리려면 컨트롤러에서 ResponseStatusException으로 변환
            throw new IllegalStateException("ALREADY_SCRAPPED");
        }
        Scrap saved = Scrap.builder()
                .member(member)
                .policyNo(policyNo)
                .build();
        scrapRepository.save(saved);
    }

    public Page<Scrap> list(Member member, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        return scrapRepository.findByMember(member, pageable);
    }

    public long count(Member member) {
        return scrapRepository.countByMember(member);
    }

    @Transactional
    public void remove(Member member, String policyNo) {
        scrapRepository.deleteByMemberAndPolicyNo(member, policyNo);
    }
}
