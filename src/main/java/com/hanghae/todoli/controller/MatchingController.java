package com.hanghae.todoli.controller;

import com.hanghae.todoli.dto.MatchingResponseDto;
import com.hanghae.todoli.dto.MatchingStateResponseDto;
import com.hanghae.todoli.security.jwt.UserDetailsImpl;
import com.hanghae.todoli.service.MatchingService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
    @ApiResponses({
            @ApiResponse(code=200, message="검색 성공"),
            @ApiResponse(code=400, message="실패"),
            @ApiResponse(code=403, message="Forbidden")
    })
    @ApiOperation(value = "사용자 검색 메소드", notes = "사용자 검색 api 입니다.")
    @GetMapping(value = "/api/users/{username}")
    public MatchingResponseDto searchMember(@PathVariable String username) {
        return matchingService.searchMember(username);
    }

    //매칭 초대기능
    @ApiResponses({
            @ApiResponse(code=200, message="초대 성공"),
            @ApiResponse(code=400, message="실패"),
            @ApiResponse(code=403, message="Forbidden")
    })
    @ApiOperation(value = "사용자 초대 메소드", notes = "사용자 초대 api 입니다.")
    @PostMapping("/api/users/invitation/{memberId}")
    public void inviteMatching(@PathVariable Long memberId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails.getMember().getMatchingState()) {
            throw new IllegalArgumentException("이미 매칭중입니다.");
        }
        System.out.println("매칭 초대 성공");
        matchingService.inviteMatching(memberId, userDetails);
    }

    //매칭 취소기능
    @ApiResponses({
            @ApiResponse(code=200, message="취소 성공"),
            @ApiResponse(code=400, message="실패"),
            @ApiResponse(code=403, message="Forbidden")
    })
    @ApiOperation(value = "사용자 취소 메소드", notes = "사용자 취소 api 입니다.")
    @PatchMapping("/api/users/cancel/{memberId}")
    public void cancelMatching(@PathVariable Long memberId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (!userDetails.getMember().getMatchingState()) {
            throw new IllegalArgumentException("매칭해야 합니다.");
        }
        matchingService.cancelMatching(memberId, userDetails);
    }

    //매칭 수락기능
    @ApiResponses({
            @ApiResponse(code=200, message="수락 성공"),
            @ApiResponse(code=400, message="실패"),
            @ApiResponse(code=403, message="Forbidden")
    })
    @ApiOperation(value = "매칭 수락 메소드", notes = "매칭 수락 api 입니다.")
    @PostMapping("/api/users/acceptance/{senderId}")
    public void acceptMatching(@PathVariable Long senderId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails.getMember().getMatchingState()) {
            throw new IllegalArgumentException("이미 매칭되었습니다.");
        }
        matchingService.acceptMatching(senderId, userDetails);
    }

    //자신의 매칭 상태 체크
    @ApiResponses({
            @ApiResponse(code=200, message="체크 성공"),
            @ApiResponse(code=400, message="실패"),
            @ApiResponse(code=403, message="Forbidden")
    })
    @ApiOperation(value = "자신의 매칭 상태 체크 메소드", notes = "자신의 매칭 상태 체크 api 입니다.")
    @GetMapping("/api/users/check")
    public MatchingStateResponseDto myMatchingState(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Boolean matchingState = userDetails.getMember().getMatchingState();

        return new MatchingStateResponseDto(matchingState);
    }
}
