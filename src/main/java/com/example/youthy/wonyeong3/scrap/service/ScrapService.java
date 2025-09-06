package com.example.youthy.wonyeong3.scrap.service;

import com.example.youthy.YouthPolicy;
import com.example.youthy.YouthPolicyRepository;
import com.example.youthy.domain.Member;
import com.example.youthy.repository.MemberRepository;
import com.example.youthy.wonyeong3.scrap.domain.Scrap;
import com.example.youthy.wonyeong3.scrap.dto.ScrapItemDto;
import com.example.youthy.wonyeong3.scrap.repository.ScrapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScrapService {

    private final ScrapRepository scrapRepository;
    private final YouthPolicyRepository youthPolicyRepository;
    private final MemberRepository memberRepository;

    /** 인증 사용자 보장 + 영속 엔티티로 재조회 */
    private Member requireManagedMember(Member m) {
        if (m == null) {
            throw new IllegalArgumentException("Unauthenticated member");
        }
        if (m.getId() != null) {
            return memberRepository.findById(m.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Member not found"));
        }
        if (m.getKakaoId() != null) {
            return memberRepository.findByKakaoId(m.getKakaoId())
                    .orElseThrow(() -> new IllegalArgumentException("Member not found"));
        }
        throw new IllegalArgumentException("Unauthenticated member");
    }

    /** 정책번호로 반드시 조회 */
    private YouthPolicy requirePolicy(String policyNo) {
        return youthPolicyRepository.findByPolicyNo(policyNo)
                .orElseThrow(() -> new IllegalArgumentException("Policy not found: " + policyNo));
    }

    @Transactional
    public void add(Member current, String policyNo) {
        Member me = requireManagedMember(current);
        YouthPolicy policy = requirePolicy(policyNo);

        if (!scrapRepository.existsByMemberAndPolicy(me, policy)) {
            scrapRepository.save(
                    Scrap.builder()
                            .member(me)
                            .policy(policy)
                            .build()
            );
        }
        // 이미 있으면 아무 것도 안 함(멱등)
    }

    @Transactional
    public void remove(Member current, String policyNo) {
        Member me = requireManagedMember(current);
        YouthPolicy policy = requirePolicy(policyNo);
        scrapRepository.deleteByMemberAndPolicy(me, policy);
    }

    @Transactional(readOnly = true)
    public Page<ScrapItemDto> list(Member current, int page, int size) {
        Member me = requireManagedMember(current);

        Pageable pageable = PageRequest.of(
                Math.max(page, 0),
                Math.max(size, 1),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<Scrap> result = scrapRepository.findByMemberOrderByCreatedAtDesc(me, pageable);

        List<ScrapItemDto> items = result.getContent().stream()
                .map(s -> ScrapItemDto.builder()
                        .policyNo(s.getPolicy().getPolicyNo())
                        .policyName(s.getPolicy().getPolicyName())
                        .viewCount(s.getPolicy().getViewCount())
                        .createdAt(s.getCreatedAt())
                        .build())
                .toList();

        return new PageImpl<>(items, pageable, result.getTotalElements());
    }

    @Transactional(readOnly = true)
    public long count(Member current) {
        Member me = requireManagedMember(current);
        return scrapRepository.countByMember(me);
    }
}
