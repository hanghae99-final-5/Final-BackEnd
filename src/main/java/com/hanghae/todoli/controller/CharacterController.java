package com.hanghae.todoli.controller;

import com.hanghae.todoli.dto.CharResponseDto;
import com.hanghae.todoli.security.jwt.UserDetailsImpl;
import com.hanghae.todoli.service.CharacterService;
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
    @GetMapping("/api/characters")
    public CharResponseDto getCharState(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return characterService.getCharState(userDetails);
    }

    //상대방 캐릭터 상태
    @GetMapping("/api/characters/partners")
    public CharResponseDto.PartnerDto getPartnerState(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return characterService.getPartnerState(userDetails);
    }
}
