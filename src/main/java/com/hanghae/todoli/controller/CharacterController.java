package com.hanghae.todoli.controller;

import com.hanghae.todoli.dto.CharResponseDto;
import com.hanghae.todoli.security.jwt.UserDetailsImpl;
import com.hanghae.todoli.service.CharacterService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Slf4j
public class CharacterController {

    private final CharacterService characterService;

    //캐릭터 상태 조회
    @ApiResponses({
            @ApiResponse(code=200, message="조회 성공"),
            @ApiResponse(code=400, message="실패"),
            @ApiResponse(code=403, message="Forbidden")
    })
    @ApiOperation(value = "캐릭터 조회 메소드", notes = "자신의 캐릭터 조회 api 입니다.")
    @GetMapping("/api/characters")
    public CharResponseDto getCharState(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return characterService.getCharState(userDetails);
    }

    //상대방 캐릭터 상태
    @ApiResponses({
            @ApiResponse(code=200, message="조회 성공"),
            @ApiResponse(code=400, message="실패"),
            @ApiResponse(code=403, message="Forbidden")
    })
    @ApiOperation(value = "캐릭터 조회 메소드", notes = "매칭된 상대방의 캐릭터 조회 api 입니다.")
    @GetMapping("/api/characters/partners")
    public CharResponseDto.PartnerDto getPartnerState(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return characterService.getPartnerState(userDetails);
    }
}
