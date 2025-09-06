package com.example.youthy.wonyeong.service;

import com.example.youthy.domain.Member;
import com.example.youthy.repository.MemberRepository;
import com.example.youthy.wonyeong.domain.WonyeongMemberProfile;
import com.example.youthy.wonyeong.dto.WonyeongMemberProfileRequest;
import com.example.youthy.wonyeong.dto.WonyeongMemberProfileResponse;
import com.example.youthy.wonyeong.repository.WonyeongMemberProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class WonyeongMemberProfileService {

    private final MemberRepository memberRepository; // 루트 Member 재사용
    private final WonyeongMemberProfileRepository profileRepository;

    /** memberId 또는 kakaoId 로 Member를 찾아 프로필을 생성/업데이트 */
    @Transactional
    public WonyeongMemberProfileResponse upsertProfile(WonyeongMemberProfileRequest req) {
        Member member = findMember(req);

        WonyeongMemberProfile profile = profileRepository.findByMember_Id(member.getId())
                .orElse(WonyeongMemberProfile.builder().member(member).build());

        profile.setAgeGroup(req.ageGroup());
        profile.setInterestedCategories(
                req.categories() == null ? new HashSet<>() : new HashSet<>(req.categories())
        );

        WonyeongMemberProfile saved = profileRepository.save(profile);

        return new WonyeongMemberProfileResponse(
                saved.getId(),
                member.getId(),
                saved.getAgeGroup(),
                saved.getInterestedCategories()
        );
    }

    private Member findMember(WonyeongMemberProfileRequest req) {
        if (req.memberId() != null) {
            return memberRepository.findById(req.memberId())
                    .orElseThrow(() -> new IllegalArgumentException("Member not found: id=" + req.memberId()));
        }
        if (req.kakaoId() != null) {
            return memberRepository.findByKakaoId(req.kakaoId())
                    .orElseThrow(() -> new IllegalArgumentException("Member not found: kakaoId=" + req.kakaoId()));
        }
        throw new IllegalArgumentException("memberId 또는 kakaoId 중 하나는 필수입니다.");
    }
}
