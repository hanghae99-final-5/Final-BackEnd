package com.hanghae.todoli.matching;

import com.hanghae.todoli.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MatchingController {

    private final MatchingService matchingService;

    //사용자 검색
    @GetMapping("/api/users/{username}")
    public MatchingResponseDto searchMember(@PathVariable String username,
                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return matchingService.searchMember(username, userDetails);
    }

    //매칭 초대기능
    @PostMapping("/api/users/invitation/{memberId}")
    public void inviteMatching(@PathVariable Long memberId,
                               @AuthenticationPrincipal UserDetailsImpl userDetails) {
        matchingService.inviteMatching(memberId, userDetails);
    }

    //매칭 취소기능
    @PatchMapping("/api/users/cancel/{memberId}")
    public void cancelMatching(@PathVariable Long memberId,
                               @AuthenticationPrincipal UserDetailsImpl userDetails) {
        matchingService.cancelMatching(memberId, userDetails);
    }

    //매칭 수락기능
    @PostMapping("/api/users/acceptance/{senderId}")
    public void acceptMatching(@PathVariable Long senderId,
                               @AuthenticationPrincipal UserDetailsImpl userDetails) {
        matchingService.acceptMatching(senderId, userDetails);
    }

    //자신의 매칭 상태 체크
    @GetMapping("/api/users/check")
    public MatchingStateResponseDto myMatchingState(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Boolean matchingState = userDetails.getMember().getMatchingState();

        return new MatchingStateResponseDto(matchingState);
    }
}
